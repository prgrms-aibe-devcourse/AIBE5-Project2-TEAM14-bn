package com.aiegoo.comicrental.dao;

import com.aiegoo.comicrental.Comic;
import java.util.List;

public interface ComicDAO {
    void add(Comic comic) throws Exception;
    List<Comic> listAll() throws Exception;
    Comic findById(int id) throws Exception;
    void update(Comic comic) throws Exception;
    void delete(int id) throws Exception;
}
