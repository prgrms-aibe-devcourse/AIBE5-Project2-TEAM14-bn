package com.aiegoo.comicrental;

import com.aiegoo.comicrental.util.DBConnectionUtil;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class RentalService {

    private final String seriesTable;     // either "gcd_series" or "comics"
    private final boolean hasIsRentedCol; // whether seriesTable contains is_rented column

    public RentalService() {
        String tbl = "comics";
        boolean col = false;
        try (Connection conn = DBConnectionUtil.getConnection()) {
            DatabaseMetaData md = conn.getMetaData();
            // check if gcd_series exists
            try (ResultSet rs = md.getTables(null, null, "gcd_series", null)) {
                if (rs.next()) {
                    tbl = "gcd_series";
                }
            }
            // check for is_rented column, and add it if missing for gcd_series
            try (ResultSet rs = md.getColumns(null, null, tbl, "is_rented")) {
                if (rs.next()) {
                    col = true;
                } else {
                    // if using gcd_series create the column automatically
                    if ("gcd_series".equals(tbl)) {
                        try (Statement s = conn.createStatement()) {
                            s.execute("ALTER TABLE gcd_series ADD COLUMN is_rented TINYINT(1) DEFAULT 0");
                        }
                        col = true;
                    }
                }
            }
        } catch (SQLException e) {
            // ignore; we'll let later operations fail if necessary
        }
        this.seriesTable = tbl;
        this.hasIsRentedCol = col;
    }

    /**
     * Rent a comic from whichever series table is in use.  Returns a Rental object representing
     * the newly-created rental record.  Throws if the comic is already rented or
     * does not exist.
     */
    public Rental rentComic(int memberId, int comicId) throws Exception {
        try (Connection conn = DBConnectionUtil.getConnection()) {
            conn.setAutoCommit(false);
            try {
                // verify comic exists and is not rented
                if (hasIsRentedCol) {
                    String check = "SELECT is_rented FROM " + seriesTable + " WHERE id = ?";
                    try (PreparedStatement stmt = conn.prepareStatement(check)) {
                        stmt.setInt(1, comicId);
                        try (ResultSet rs = stmt.executeQuery()) {
                            if (!rs.next()) {
                                throw new IllegalArgumentException("Comic id " + comicId + " not found in " + seriesTable);
                            }
                            if (rs.getInt("is_rented") == 1) {
                                throw new IllegalStateException("Comic " + comicId + " is already rented");
                            }
                        }
                    }
                }

                // insert rental record
                String insert = "INSERT INTO rentals(member_id, comic_id, status) VALUES (?, ?, 'RENTED')";
                int rentalId;
                try (PreparedStatement stmt = conn.prepareStatement(insert, Statement.RETURN_GENERATED_KEYS)) {
                    stmt.setInt(1, memberId);
                    stmt.setInt(2, comicId);
                    stmt.executeUpdate();
                    try (ResultSet keys = stmt.getGeneratedKeys()) {
                        if (keys.next()) {
                            rentalId = keys.getInt(1);
                        } else {
                            throw new Exception("Failed to retrieve generated rental id");
                        }
                    }
                }

                // mark the comic as rented
                if (hasIsRentedCol) {
                    String update = "UPDATE " + seriesTable + " SET is_rented = 1 WHERE id = ?";
                    try (PreparedStatement stmt = conn.prepareStatement(update)) {
                        stmt.setInt(1, comicId);
                        stmt.executeUpdate();
                    }
                }

                conn.commit();

                Rental r = new Rental();
                r.setId(rentalId);
                r.setMemberId(memberId);
                r.setComicId(comicId);
                r.setStatus("RENTED");
                r.setDueDate(java.time.LocalDateTime.now().plusDays(7));
                return r;
            } catch (Exception ex) {
                conn.rollback();
                throw ex;
            } finally {
                conn.setAutoCommit(true);
            }
        }
    }

    /**
     * Return a comic given an existing rental id.  Updates both the rental row and
     * resets the corresponding gcd_series.is_rented flag.  Does nothing if the
     * rental is already marked RETURNED.
     */
    public void returnComic(int rentalId) throws Exception {
        try (Connection conn = DBConnectionUtil.getConnection()) {
            conn.setAutoCommit(false);
            try {
                // fetch rental information
                String sel = "SELECT comic_id, status FROM rentals WHERE id = ?";
                int comicId;
                String status;
                try (PreparedStatement stmt = conn.prepareStatement(sel)) {
                    stmt.setInt(1, rentalId);
                    try (ResultSet rs = stmt.executeQuery()) {
                        if (!rs.next()) {
                            throw new IllegalArgumentException("Rental id " + rentalId + " not found");
                        }
                        comicId = rs.getInt("comic_id");
                        status = rs.getString("status");
                    }
                }

                if ("RETURNED".equals(status)) {
                    // nothing to do
                    conn.commit();
                    return;
                }

                // update rental record
                String upd = "UPDATE rentals SET returned_at = CURRENT_TIMESTAMP, status = 'RETURNED' WHERE id = ?";
                try (PreparedStatement stmt = conn.prepareStatement(upd)) {
                    stmt.setInt(1, rentalId);
                    stmt.executeUpdate();
                }

                // unmark the comic if we have the flag column
                if (hasIsRentedCol) {
                    String updateComic = "UPDATE " + seriesTable + " SET is_rented = 0 WHERE id = ?";
                    try (PreparedStatement stmt = conn.prepareStatement(updateComic)) {
                        stmt.setInt(1, comicId);
                        stmt.executeUpdate();
                    }
                }

                conn.commit();
            } catch (Exception ex) {
                conn.rollback();
                throw ex;
            } finally {
                conn.setAutoCommit(true);
            }
        }
    }
}
