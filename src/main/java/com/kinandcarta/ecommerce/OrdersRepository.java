package com.kinandcarta.ecommerce;

import com.kinandcarta.ecommerce.entities.Orders;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrdersRepository extends JpaRepository<Orders, Long> {
}
