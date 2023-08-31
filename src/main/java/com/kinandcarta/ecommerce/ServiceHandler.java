package com.kinandcarta.ecommerce;

import java.util.Set;

public interface ServiceHandler {    
    Orders create(final Orders model);
    Orders update(final Long id, final Orders model);
    void delete(final Long id);
    Orders findById(final Long id);

    Set<Orders> findAll();
    
}
