package com.springboot.store.exceptions;

public class CartNotFoundException extends RuntimeException {
    public CartNotFoundException(){
        super("Cart Not Found");
    }

}
