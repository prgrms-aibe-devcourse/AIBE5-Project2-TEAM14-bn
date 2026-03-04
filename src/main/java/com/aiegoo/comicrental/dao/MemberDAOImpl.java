package com.aiegoo.comicrental.dao;

import com.aiegoo.comicrental.Member;
import com.aiegoo.comicrental.util.DBConnectionUtil;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class MemberDAOImpl implements MemberDAO {

    @Override
    public void add(Member member) throws Exception {
        String sql = "INSERT INTO members(name, phone_number) VALUES (?, ?)";
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
        String sql = "SELECT id, name, phone_number FROM members ORDER BY id";
        List<Member> list = new ArrayList<>();
        try (Connection conn = DBConnectionUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                Member m = new Member();
                m.setId(rs.getInt("id"));
                m.setName(rs.getString("name"));
                m.setPhone(rs.getString("phone_number"));
                list.add(m);
            }
        }
        return list;
    }
}
