package com.springboot.store.controllers;


import com.springboot.store.dtos.ErrorDto;
import com.springboot.store.dtos.OrderDto;
import com.springboot.store.exceptions.OrderNotFoundException;

import com.springboot.store.services.OrderService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.access.AccessDeniedException;

import java.util.List;

@AllArgsConstructor
@RestController
@RequestMapping("/api/orders")
public class OrderController {
    private final OrderService orderService;

    @GetMapping
    public List<OrderDto> getAllOrders(){
      return orderService.getAllOrders();
  }

    @GetMapping("/{orderId}")
    public OrderDto getOrder(
            @PathVariable("orderId") Long orderId
                             ){
      return  orderService.getOrder(orderId);
    }

    @ExceptionHandler(OrderNotFoundException.class)
    public ResponseEntity<Void> handleException(){
        return ResponseEntity.notFound().build();
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ErrorDto> handleAccessDenied(Exception ex){
        return ResponseEntity
                .status(HttpStatus.FORBIDDEN)
                .body(new ErrorDto(ex.getMessage()));
    }


}
