package com.aiegoo.comicrental.dao;

import com.aiegoo.comicrental.Comic;
import com.aiegoo.comicrental.util.DBConnectionUtil;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ComicDAOImpl implements ComicDAO {
    @Override
    public void add(Comic comic) throws Exception {
        String sql = "INSERT INTO comics(title, volume_count, author, is_rented) VALUES (?, ?, ?, ?)";
        try (Connection conn = DBConnectionUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, comic.getTitle());
            stmt.setInt(2, comic.getVolume());
            stmt.setString(3, comic.getAuthor());
            stmt.setBoolean(4, comic.isRented());

            int affected = stmt.executeUpdate();
            if (affected == 0) {
                throw new SQLException("Creating comic failed, no rows affected.");
            }

            try (ResultSet keys = stmt.getGeneratedKeys()) {
                if (keys.next()) {
                    comic.setId(keys.getInt(1));
                }
            }
        }
    }

    @Override
    public List<Comic> listAll() throws Exception {
        String sql = "SELECT id, title, volume_count, author, is_rented FROM comics ORDER BY id";
        return executeComicQuery(sql);
    }

    @Override
    public List<Comic> search(String keyword) throws Exception {
        String sql = "SELECT id, title, volume_count, author, is_rented " +
                     "FROM comics WHERE title LIKE ? OR author LIKE ? ORDER BY id";
        List<Comic> list = new ArrayList<>();
        String pattern = "%" + keyword + "%";
        try (Connection conn = DBConnectionUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, pattern);
            stmt.setString(2, pattern);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Comic c = new Comic();
                    c.setId(rs.getInt("id"));
                    c.setTitle(rs.getString("title"));
                    c.setVolume(rs.getInt("volume_count"));
                    c.setAuthor(rs.getString("author"));
                    c.setRented(rs.getBoolean("is_rented"));
                    list.add(c);
                }
            }
        }
        return list;
    }

    private List<Comic> executeComicQuery(String sql) throws Exception {
        List<Comic> list = new ArrayList<>();
        try (Connection conn = DBConnectionUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                Comic c = new Comic();
                c.setId(rs.getInt("id"));
                c.setTitle(rs.getString("title"));
                c.setVolume(rs.getInt("volume_count"));
                c.setAuthor(rs.getString("author"));
                c.setRented(rs.getBoolean("is_rented"));
                list.add(c);
            }
        }
        return list;
    }

    @Override
    public Comic findById(int id) throws Exception {
        String sql = "SELECT id, title, volume_count, author, is_rented FROM comics WHERE id = ?";
        try (Connection conn = DBConnectionUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    Comic c = new Comic();
                    c.setId(rs.getInt("id"));
                    c.setTitle(rs.getString("title"));
                    c.setVolume(rs.getInt("volume_count"));
                    c.setAuthor(rs.getString("author"));
                    c.setRented(rs.getBoolean("is_rented"));
                    return c;
                }
            }
        }
        return null;
    }

    @Override
    public void update(Comic comic) throws Exception {
        String sql = "UPDATE comics SET title = ?, volume_count = ?, author = ?, is_rented = ? WHERE id = ?";
        try (Connection conn = DBConnectionUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, comic.getTitle());
            stmt.setInt(2, comic.getVolume());
            stmt.setString(3, comic.getAuthor());
            stmt.setBoolean(4, comic.isRented());
            stmt.setInt(5, comic.getId());
            stmt.executeUpdate();
        }
    }

    @Override
    public void delete(int id) throws Exception {
        String sql = "DELETE FROM comics WHERE id = ?";
        try (Connection conn = DBConnectionUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            stmt.executeUpdate();
        }
    }
}
