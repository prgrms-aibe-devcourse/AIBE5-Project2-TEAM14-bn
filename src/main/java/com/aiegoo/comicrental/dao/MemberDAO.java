package com.aiegoo.comicrental.dao;

import com.aiegoo.comicrental.Member;
import java.util.List;

public interface MemberDAO {
    void add(Member member) throws Exception;
    List<Member> listAll() throws Exception;
}
