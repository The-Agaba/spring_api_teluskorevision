package com.springboot.store.controllers;

import com.springboot.store.dtos.AddItemToCartRequest;
import com.springboot.store.dtos.CartDto;
import com.springboot.store.dtos.CartItemDto;
import com.springboot.store.dtos.UpdateCartItemRequest;

import com.springboot.store.exceptions.CartNotFoundException;

import com.springboot.store.exceptions.ProductNotFoundException;
import com.springboot.store.services.CartService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.Map;
import java.util.UUID;

@RestController
@AllArgsConstructor
@RequestMapping("api/carts")
@Tag(name="Carts")  // this is used for renaming field in spring doc

public class CartController {
    private final CartService cartService;

    @PostMapping
    public ResponseEntity<CartDto> createCart(
            UriComponentsBuilder uriBuilder
    ){
        var cartDto=cartService.createCart();
        var uri= uriBuilder.path("/carts/{id}").buildAndExpand(cartDto.getId()).toUri();



        return ResponseEntity.created(uri).body(cartDto);
    }

    @PostMapping("/{cartId}/items")
    @Operation(description = "This adds product to cart") // this is used to add description to an endpoint
    public ResponseEntity<CartItemDto> addtoCart(
            @PathVariable UUID cartId,
            @RequestBody AddItemToCartRequest request
    ){
       var cartItemDto=cartService.addToCart(cartId,request.getProductId());
       return ResponseEntity.status(HttpStatus.CREATED).body(cartItemDto);
    }

    @GetMapping("/{cartId}")
    public CartDto getCart(
            @PathVariable UUID cartId
    ){
      return cartService.getCart(cartId);

    }

    @PutMapping("{cartId}/items/{productId}")
    public CartItemDto updateItem(
            @PathVariable("cartId") UUID cartId,
            @PathVariable("productId") Long productId,
            @Valid @RequestBody UpdateCartItemRequest request
            ){
        return cartService.updateItem(cartId,productId,request.getQuantity());

    }

    @DeleteMapping("{cartId}/items/{productId}")
    public ResponseEntity<?> removeItem(
            @PathVariable("cartId") UUID cartId,
            @PathVariable("productId") Long productId
    ){
        cartService.removeItem(cartId,productId);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{cartId}/items")
    public ResponseEntity<Void> clearCart(@PathVariable UUID cartId){
        cartService.clearCart(cartId);
        return ResponseEntity.noContent().build();
    }

   @ExceptionHandler(CartNotFoundException.class)
    public ResponseEntity<Map<String,String>> handleCartNotFound(){
        return  ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", "Cart Not Found"));
    }

    @ExceptionHandler(ProductNotFoundException.class)
    public ResponseEntity<Map<String,String>> handleProductNotFound(){
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error","Product Not Found In Cart "));
    }
}
