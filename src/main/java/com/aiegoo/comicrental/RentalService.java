package com.aiegoo.comicrental;

import com.aiegoo.comicrental.util.DBConnectionUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;

public class RentalService {

    /**
     * Rent a comic from the gcd_series table.  Returns a Rental object representing
     * the newly-created rental record.  Throws if the comic is already rented or
     * does not exist.
     */
    public Rental rentComic(int memberId, int comicId) throws Exception {
        try (Connection conn = DBConnectionUtil.getConnection()) {
            conn.setAutoCommit(false);
            try {
                // verify comic exists and is not rented
                String check = "SELECT is_rented FROM gcd_series WHERE id = ?";
                try (PreparedStatement stmt = conn.prepareStatement(check)) {
                    stmt.setInt(1, comicId);
                    try (ResultSet rs = stmt.executeQuery()) {
                        if (!rs.next()) {
                            throw new IllegalArgumentException("Comic id " + comicId + " not found in gcd_series");
                        }
                        if (rs.getInt("is_rented") == 1) {
                            throw new IllegalStateException("Comic " + comicId + " is already rented");
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
                String update = "UPDATE gcd_series SET is_rented = 1 WHERE id = ?";
                try (PreparedStatement stmt = conn.prepareStatement(update)) {
                    stmt.setInt(1, comicId);
                    stmt.executeUpdate();
                }

                conn.commit();

                Rental r = new Rental();
                r.setId(rentalId);
                r.setMemberId(memberId);
                r.setComicId(comicId);
                r.setStatus("RENTED");
                // assume due_date default is 7 days; mirror that in object
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

                // unmark the comic
                String updateComic = "UPDATE gcd_series SET is_rented = 0 WHERE id = ?";
                try (PreparedStatement stmt = conn.prepareStatement(updateComic)) {
                    stmt.setInt(1, comicId);
                    stmt.executeUpdate();
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
