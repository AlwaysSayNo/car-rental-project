package com.epam.nazar.grinko.exceptions;

public class OrdersOverflowException extends RuntimeException {

    public OrdersOverflowException() {
        super("You tried to exceed the number of orders");
    }

    public OrdersOverflowException(String msg) {
        super(msg);
    }
}
