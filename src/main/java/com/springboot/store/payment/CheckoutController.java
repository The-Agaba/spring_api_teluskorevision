package com.springboot.store.payment;

import com.springboot.store.dtos.ErrorDto;
import com.springboot.store.exceptions.CartEmptyException;
import com.springboot.store.exceptions.CartNotFoundException;
import com.springboot.store.repositories.OrderRepository;
import jakarta.validation.Valid;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/checkout")
@RequiredArgsConstructor
public class CheckoutController {
    private final CheckoutService checkoutService;
    private final OrderRepository orderRepository;


    @PostMapping
     public CheckoutResponse checkout(
            @Valid @RequestBody CheckoutRequest request
    ) {
            return checkoutService.checkout(request);
    }

    @PostMapping("/webhook")
   public void  handleWebhook(
            @RequestHeader Map<String,String> headers,
            @RequestBody String payload
   ) {
        checkoutService.handleWebhookEvent(new WebhookRequest(headers, payload));
    }

    @ExceptionHandler(PaymentException.class)
    public ResponseEntity<?> handlePaymentException (){
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ErrorDto("Error creating a checkout session"));
    }

    @ExceptionHandler({CartNotFoundException.class, CartEmptyException.class})
    public ResponseEntity<ErrorDto> handleException(
          Exception e
    ){
        return ResponseEntity.badRequest().body(
                new ErrorDto(e.getMessage())
        );
    }
}
