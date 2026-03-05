package com.aiegoo.comicrental.dao;

import com.aiegoo.comicrental.Rental;
import com.aiegoo.comicrental.util.DBConnectionUtil;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class RentalDAOImpl implements RentalDAO {
    @Override
    public void add(Rental rental) throws Exception {
        String sql = "INSERT INTO rentals(comic_id, member_id, status, due_date) VALUES (?, ?, ?, ?)";
        try (Connection conn = DBConnectionUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setInt(1, rental.getComicId());
            stmt.setInt(2, rental.getMemberId());
            stmt.setString(3, rental.getStatus());
            if (rental.getDueDate() != null) {
                stmt.setTimestamp(4, Timestamp.valueOf(rental.getDueDate()));
            } else {
                stmt.setNull(4, Types.TIMESTAMP);
            }

            int affected = stmt.executeUpdate();
            if (affected == 0) {
                throw new SQLException("Creating rental failed, no rows affected.");
            }

            try (ResultSet keys = stmt.getGeneratedKeys()) {
                if (keys.next()) {
                    rental.setId(keys.getInt(1));
                }
            }
        }
    }

    @Override
    public List<Rental> listAll() throws Exception {
        String sql = "SELECT id, comic_id, member_id, rented_at, due_date, returned_at, status FROM rentals ORDER BY id";
        return queryRentals(sql);
    }

    @Override
    public List<Rental> listOpen() throws Exception {
        String sql = "SELECT id, comic_id, member_id, rented_at, due_date, returned_at, status FROM rentals WHERE status <> 'RETURNED' ORDER BY id";
        return queryRentals(sql);
    }

    public List<Rental> findByMember(int memberId) throws Exception {
        String sql = "SELECT id, comic_id, member_id, rented_at, due_date, returned_at, status " +
                     "FROM rentals WHERE member_id = ? ORDER BY id";
        List<Rental> list = new ArrayList<>();
        try (Connection conn = DBConnectionUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, memberId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Rental r = new Rental();
                    r.setId(rs.getInt("id"));
                    r.setComicId(rs.getInt("comic_id"));
                    r.setMemberId(rs.getInt("member_id"));
                    Timestamp rented = rs.getTimestamp("rented_at");
                    if (rented != null) r.setRentedAt(rented.toLocalDateTime());
                    Timestamp due = rs.getTimestamp("due_date");
                    if (due != null) r.setDueDate(due.toLocalDateTime());
                    Timestamp returned = rs.getTimestamp("returned_at");
                    if (returned != null) r.setReturnedAt(returned.toLocalDateTime());
                    r.setStatus(rs.getString("status"));
                    list.add(r);
                }
            }
        }
        return list;
    }

    private List<Rental> queryRentals(String sql) throws Exception {
        List<Rental> list = new ArrayList<>();
        try (Connection conn = DBConnectionUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                Rental r = new Rental();
                r.setId(rs.getInt("id"));
                r.setComicId(rs.getInt("comic_id"));
                r.setMemberId(rs.getInt("member_id"));
                Timestamp rented = rs.getTimestamp("rented_at");
                if (rented != null) r.setRentedAt(rented.toLocalDateTime());
                Timestamp due = rs.getTimestamp("due_date");
                if (due != null) r.setDueDate(due.toLocalDateTime());
                Timestamp returned = rs.getTimestamp("returned_at");
                if (returned != null) r.setReturnedAt(returned.toLocalDateTime());
                r.setStatus(rs.getString("status"));
                list.add(r);
            }
        }
        return list;
    }

    @Override
    public Rental findById(int id) throws Exception {
        String sql = "SELECT id, comic_id, member_id, rented_at, due_date, returned_at, status FROM rentals WHERE id = ?";
        try (Connection conn = DBConnectionUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    Rental r = new Rental();
                    r.setId(rs.getInt("id"));
                    r.setComicId(rs.getInt("comic_id"));
                    r.setMemberId(rs.getInt("member_id"));
                    Timestamp rented = rs.getTimestamp("rented_at");
                    if (rented != null) r.setRentedAt(rented.toLocalDateTime());
                    Timestamp due = rs.getTimestamp("due_date");
                    if (due != null) r.setDueDate(due.toLocalDateTime());
                    Timestamp returned = rs.getTimestamp("returned_at");
                    if (returned != null) r.setReturnedAt(returned.toLocalDateTime());
                    r.setStatus(rs.getString("status"));
                    return r;
                }
            }
        }
        return null;
    }

    @Override
    public void update(Rental rental) throws Exception {
        String sql = "UPDATE rentals SET comic_id = ?, member_id = ?, rented_at = ?, due_date = ?, returned_at = ?, status = ? WHERE id = ?";
        try (Connection conn = DBConnectionUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, rental.getComicId());
            stmt.setInt(2, rental.getMemberId());
            stmt.setTimestamp(3, Timestamp.valueOf(rental.getRentedAt()));
            if (rental.getDueDate() != null) {
                stmt.setTimestamp(4, Timestamp.valueOf(rental.getDueDate()));
            } else {
                stmt.setNull(4, Types.TIMESTAMP);
            }
            if (rental.getReturnedAt() != null) {
                stmt.setTimestamp(5, Timestamp.valueOf(rental.getReturnedAt()));
            } else {
                stmt.setNull(5, Types.TIMESTAMP);
            }
            stmt.setString(6, rental.getStatus());
            stmt.setInt(7, rental.getId());
            stmt.executeUpdate();
        }
    }
}
