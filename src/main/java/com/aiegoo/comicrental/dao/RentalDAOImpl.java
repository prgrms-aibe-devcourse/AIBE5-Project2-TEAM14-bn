package com.aiegoo.comicrental.dao;

import com.aiegoo.comicrental.Rental;
import com.aiegoo.comicrental.util.DBConnectionUtil;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class RentalDAOImpl implements RentalDAO {
    @Override
    public void add(Rental rental) throws Exception {
        String sql = "INSERT INTO rentals(comic_id, member_id, status) VALUES (?, ?, ?)";
        try (Connection conn = DBConnectionUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setInt(1, rental.getComicId());
            stmt.setInt(2, rental.getMemberId());
            stmt.setString(3, rental.getStatus());

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
        String sql = "SELECT id, comic_id, member_id, rented_at, returned_at, status FROM rentals ORDER BY id";
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
        String sql = "SELECT id, comic_id, member_id, rented_at, returned_at, status FROM rentals WHERE id = ?";
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
        String sql = "UPDATE rentals SET comic_id = ?, member_id = ?, rented_at = ?, returned_at = ?, status = ? WHERE id = ?";
        try (Connection conn = DBConnectionUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, rental.getComicId());
            stmt.setInt(2, rental.getMemberId());
            stmt.setTimestamp(3, Timestamp.valueOf(rental.getRentedAt()));
            if (rental.getReturnedAt() != null) {
                stmt.setTimestamp(4, Timestamp.valueOf(rental.getReturnedAt()));
            } else {
                stmt.setNull(4, Types.TIMESTAMP);
            }
            stmt.setString(5, rental.getStatus());
            stmt.setInt(6, rental.getId());
            stmt.executeUpdate();
        }
    }
}
