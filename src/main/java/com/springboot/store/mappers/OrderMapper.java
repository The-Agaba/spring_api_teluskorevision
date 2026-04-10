package com.springboot.store.mappers;


import com.springboot.store.dtos.OrderDto;
import com.springboot.store.entities.Order;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface OrderMapper {
    OrderDto toDto(Order order);
}
