package com.aiegoo.comicrental;

import com.aiegoo.comicrental.dao.ComicDAO;
import com.aiegoo.comicrental.dao.ComicDAOImpl;

import java.util.List;

/**
 * Repository layer for comic operations. Wraps the DAO and provides
 * methods aligned with application commands.
 */
public class ComicRepository {
    private final ComicDAO dao = new ComicDAOImpl();

    public Comic addComic(Comic comic) throws Exception {
        dao.add(comic);
        return comic; // id populated by DAO
    }

    public List<Comic> listComics() throws Exception {
        return dao.listAll();
    }

    public Comic showComicDetail(int id) throws Exception {
        return dao.findById(id);
    }

    public void updateComic(Comic comic) throws Exception {
        dao.update(comic);
    }

    public void deleteComic(int id) throws Exception {
        dao.delete(id);
    }
}
