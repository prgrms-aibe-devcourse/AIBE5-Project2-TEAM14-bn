package com.aiegoo.comicrental;

import com.aiegoo.comicrental.dao.RentalDAO;
import com.aiegoo.comicrental.dao.RentalDAOImpl;

import java.util.List;

public class RentalRepository {
    private final RentalDAO dao = new RentalDAOImpl();

    public Rental addRental(Rental rental) throws Exception {
        dao.add(rental);
        return rental;
    }

    // convenience aliases used by App
    public Rental add(Rental rental) throws Exception {
        return addRental(rental);
    }

    public List<Rental> listAll() throws Exception {
        return dao.listAll();
    }

    public List<Rental> listOpen() throws Exception {
        return dao.listOpen();
    }

    public List<Rental> findByMember(int memberId) throws Exception {
        return dao.findByMember(memberId);
    }

    public Rental findById(int id) throws Exception {
        return dao.findById(id);
    }

    public void updateRental(Rental rental) throws Exception {
        dao.update(rental);
    }

    public void update(Rental rental) throws Exception {
        updateRental(rental);
    }
}