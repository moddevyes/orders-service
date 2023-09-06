package com.kinandcarta.ecommerce.infrastructure;

import com.kinandcarta.ecommerce.entities.OrdersAddress;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrdersAddressRepository extends JpaRepository<OrdersAddress, Long> {
}
