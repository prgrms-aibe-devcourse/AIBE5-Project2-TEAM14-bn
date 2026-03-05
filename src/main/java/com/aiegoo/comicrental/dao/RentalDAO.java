package com.aiegoo.comicrental.dao;

import com.aiegoo.comicrental.Rental;
import java.util.List;

public interface RentalDAO {
    void add(Rental rental) throws Exception;
    List<Rental> listAll() throws Exception;
    /**
     * Return only rentals that are not yet returned (status != 'RETURNED').
     */
    List<Rental> listOpen() throws Exception;
    /**
     * Rentals for a specific member.
     */
    List<Rental> findByMember(int memberId) throws Exception;
    Rental findById(int id) throws Exception;
    void update(Rental rental) throws Exception;
}
