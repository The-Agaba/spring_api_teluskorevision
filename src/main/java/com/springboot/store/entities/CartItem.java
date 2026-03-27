package com.springboot.store.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@Entity
@Table(name = "cart_items")
public class CartItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;


    @ManyToOne
    //(fetch = FetchType.LAZY,optional = false)
   // @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "cart_id")
            //, nullable = false)
    private Cart cart;

    //@NotNull
    @ManyToOne
    @JoinColumn(name = "product_id")//, nullable = false)
    private Product product;

    //@NotNull
    //@ColumnDefault("1")
    @Column(name = "quantity") //nullable = false)
    private Integer quantity;

   public BigDecimal getTotalPrice(){
       return product.getPrice().multiply(BigDecimal.valueOf(quantity));
   }
}