package com.kinandcarta.ecommerce.infrastructure;

import com.kinandcarta.ecommerce.entities.Orders;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrdersRepository extends JpaRepository<Orders, Long> {
}
