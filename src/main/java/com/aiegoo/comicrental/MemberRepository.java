package com.aiegoo.comicrental;

import com.aiegoo.comicrental.dao.MemberDAO;
import com.aiegoo.comicrental.dao.MemberDAOImpl;

import java.util.List;

public class MemberRepository {
    private final MemberDAO dao;

    public MemberRepository() {
        try {
            dao = new MemberDAOImpl();
        } catch (Exception e) {
            throw new RuntimeException("failed to initialize DAO", e);
        }
    }

    public Member addMember(Member member) throws Exception {
        dao.add(member);
        return member;
    }

    public List<Member> listMembers() throws Exception {
        return dao.listAll();
    }

    public Member findByPhone(String phone) throws Exception {
        return dao.findByPhone(phone);
    }
}
