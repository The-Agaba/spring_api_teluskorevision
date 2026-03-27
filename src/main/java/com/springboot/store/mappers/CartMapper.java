package com.springboot.store.mappers;

import com.springboot.store.dtos.CartDto;
import com.springboot.store.dtos.CartItemDto;
import com.springboot.store.entities.Cart;
import com.springboot.store.entities.CartItem;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface CartMapper {
    @Mapping(target = "items", source= "items")//mapping not needed anymore
    @Mapping(target = "totalPrice", expression = "java(cart.getTotalPrice())")
    CartDto toDto(Cart cart);

    @Mapping(target = "totalPrice", expression = "java(cartItem.getTotalPrice())")
    CartItemDto toDto(CartItem cartItem);
}
