package com.springboot.store.entities;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Table(name = "order_items")
@Getter
@Setter
@NoArgsConstructor
public class OrderItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    // order_id → Order (relation to our Order entity)
    @ManyToOne
    @JoinColumn(name = "order_id")
    private Order order;

    // product_id → Product
    @ManyToOne
    @JoinColumn(name = "product_id")
    private Product product;

    @Column(name = "unit_price")
    private BigDecimal unitPrice;

    @Column(nullable = false)
    private Integer quantity;

    @Column(name = "total_price")
    private BigDecimal totalPrice;

    public OrderItem(Order order, Product product, Integer quantity) {
        this.order=order;
        this.product=product;
        this.quantity=quantity;
        this.unitPrice=product.getPrice();
        this.totalPrice=unitPrice.multiply(BigDecimal.valueOf(quantity));
    }
}
