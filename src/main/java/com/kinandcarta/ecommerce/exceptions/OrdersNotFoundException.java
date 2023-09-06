package com.kinandcarta.ecommerce.exceptions;

public class OrdersNotFoundException extends RuntimeException {
    public OrdersNotFoundException(String message) {
        super(message);
    }
}
