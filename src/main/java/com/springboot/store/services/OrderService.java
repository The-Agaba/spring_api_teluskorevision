package com.springboot.store.services;

import com.springboot.store.dtos.OrderDto;
import com.springboot.store.exceptions.CartNotFoundException;
import com.springboot.store.exceptions.OrderNotFoundException;
import com.springboot.store.mappers.OrderMapper;
import com.springboot.store.repositories.OrderRepository;
import lombok.AllArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class OrderService {
    private final AuthService authService;
    private final OrderRepository orderRepository;
    private final OrderMapper orderMapper;

    public List<OrderDto> getAllOrders(){
        var user=authService.getCurrentUSer();
        var orders =orderRepository.getOrderByCustomer(user);
        return orders.stream().map(orderMapper::toDto).toList();
    }

    public OrderDto getOrder(Long orderId) {
        var order = orderRepository.getOrderWithItems(orderId).orElseThrow(
                OrderNotFoundException::new);
        var user= authService.getCurrentUSer();
        if(!order.getCustomer().getId().equals(user.getId())){
            throw new AccessDeniedException("You Don't have access to this order");
        }

        return orderMapper.toDto(order);
    }
}
