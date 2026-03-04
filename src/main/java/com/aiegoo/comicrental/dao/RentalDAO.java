package com.aiegoo.comicrental.dao;

import com.aiegoo.comicrental.Rental;
import java.util.List;

public interface RentalDAO {
    void add(Rental rental) throws Exception;
    List<Rental> listAll() throws Exception;
    Rental findById(int id) throws Exception;
    void update(Rental rental) throws Exception;
}
