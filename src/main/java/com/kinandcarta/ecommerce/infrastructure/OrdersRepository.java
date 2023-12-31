package com.kinandcarta.ecommerce.infrastructure;

import com.kinandcarta.ecommerce.entities.Orders;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OrdersRepository extends JpaRepository<Orders, Long> {
    List<Orders> findAllByOrdersAccountIdOrderByOrderDateDesc(final Long id);
}
