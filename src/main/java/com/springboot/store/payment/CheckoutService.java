package com.springboot.store.payment;

import com.springboot.store.entities.Order;
import com.springboot.store.exceptions.CartEmptyException;
import com.springboot.store.exceptions.CartNotFoundException;
import com.springboot.store.repositories.CartRepository;
import com.springboot.store.repositories.OrderRepository;


import com.springboot.store.services.AuthService;
import com.springboot.store.services.CartService;
import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CheckoutService {
    private final CartRepository cartRepository;
    private final OrderRepository orderRepository;
    private final AuthService authService;
    private final CartService cartService;
    private final PaymentGateway paymentGateway;



    public CheckoutResponse checkout(CheckoutRequest request)  {

        var cart=cartRepository.getCartWithItems(request.getCartId()).orElse(null);
        if(cart==null){
            throw new CartNotFoundException();

        }

        if(cart.isEmpty()){
            throw new CartEmptyException();
        }

        var order= Order.fromCart(cart,authService.getCurrentUSer());
        orderRepository.save(order);


       try {
           var session =paymentGateway.createCheckoutSession(order);
           cartService.clearCart(cart.getId());
           return new CheckoutResponse(order.getId(),session.getCheckoutUrl());

       } catch (PaymentException ex) {
           orderRepository.delete(order);
           throw ex;
       }
    }

    public void handleWebhookEvent(WebhookRequest request){
        paymentGateway
                .parseWebhookRequest(request)
                .ifPresent(paymentResult -> {
                    var order = orderRepository.findById(paymentResult.getOrderId()).orElseThrow();
                    //order.setStatus(PaymentStatus.PAID);
                    order.setStatus(paymentResult.getPaymentStatus());
                    orderRepository.save(order);
                });

    }

}
