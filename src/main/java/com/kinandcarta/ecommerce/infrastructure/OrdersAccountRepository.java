package com.kinandcarta.ecommerce.infrastructure;

import com.kinandcarta.ecommerce.entities.OrdersAccount;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrdersAccountRepository extends JpaRepository<OrdersAccount, Long> {
}
