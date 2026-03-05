package com.aiegoo.comicrental.dao;

import com.aiegoo.comicrental.Member;
import java.util.List;

public interface MemberDAO {
    void add(Member member) throws Exception;
    List<Member> listAll() throws Exception;
    /**
     * Look up a member by phone number, returning null if not found.
     */
    Member findByPhone(String phone) throws Exception;
}
