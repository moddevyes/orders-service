package com.kinandcarta.ecommerce.contracts;

import com.kinandcarta.ecommerce.entities.Orders;
import com.kinandcarta.ecommerce.exceptions.OrderModelNotPersistedException;

import java.util.Set;

public interface ServiceHandler {    
    Orders create(final Orders model) throws OrderModelNotPersistedException;
    Orders update(final Long id, final Orders model);
    void delete(final Long id);
    Orders findById(final Long id);

    Set<Orders> findAll();
    
}
