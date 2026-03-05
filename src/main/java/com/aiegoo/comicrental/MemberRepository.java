package com.aiegoo.comicrental;

import com.aiegoo.comicrental.dao.MemberDAO;
import com.aiegoo.comicrental.dao.MemberDAOImpl;

import java.util.List;

public class MemberRepository {
    private final MemberDAO dao = new MemberDAOImpl();

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
