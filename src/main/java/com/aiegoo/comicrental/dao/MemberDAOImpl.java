package com.aiegoo.comicrental.dao;

import com.aiegoo.comicrental.Member;
import com.aiegoo.comicrental.util.DBConnectionUtil;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class MemberDAOImpl implements MemberDAO {

    @Override
    public void add(Member member) throws Exception {
        String sql = "INSERT INTO members(name, phone) VALUES (?, ?)";
        try (Connection conn = DBConnectionUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, member.getName());
            stmt.setString(2, member.getPhone());

            int affected = stmt.executeUpdate();
            if (affected == 0) {
                throw new SQLException("Creating member failed, no rows affected.");
            }

            try (ResultSet keys = stmt.getGeneratedKeys()) {
                if (keys.next()) {
                    member.setId(keys.getInt(1));
                }
            }
        }
    }

    @Override
    public List<Member> listAll() throws Exception {
        String sql = "SELECT id, name, phone, join_date FROM members ORDER BY id";
        List<Member> list = new ArrayList<>();
        try (Connection conn = DBConnectionUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                Member m = new Member();
                m.setId(rs.getInt("id"));
                m.setName(rs.getString("name"));
                m.setPhone(rs.getString("phone"));
                Date jd = rs.getDate("join_date");
                if (jd != null) {
                    m.setRegDate(jd.toLocalDate());
                }
                list.add(m);
            }
        }
        return list;
    }

    @Override
    public Member findByPhone(String phone) throws Exception {
        String sql = "SELECT id, name, phone, join_date FROM members WHERE phone = ?";
        try (Connection conn = DBConnectionUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, phone);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    Member m = new Member();
                    m.setId(rs.getInt("id"));
                    m.setName(rs.getString("name"));
                    m.setPhone(rs.getString("phone"));
                    Date jd = rs.getDate("join_date");
                    if (jd != null) {
                        m.setRegDate(jd.toLocalDate());
                    }
                    return m;
                }
            }
        }
        return null;
    }
}
