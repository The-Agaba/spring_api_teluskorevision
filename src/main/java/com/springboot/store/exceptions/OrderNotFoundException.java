package com.springboot.store.exceptions;

public class OrderNotFoundException extends RuntimeException {
    public OrderNotFoundException(){
        super("Order Not Found");
    }
}
