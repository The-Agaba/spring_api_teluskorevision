package com.springboot.store.entities;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.LinkedHashSet;
import java.util.Set;

@Entity
@Table(name = "orders")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name ="id")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "customer_id")
    private User customer;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private PaymentStatus status;

    @Column(name = "created_at", nullable = false)
    @CreationTimestamp
    private LocalDateTime createdAt;



    @Column(name = "total_price")
    private BigDecimal totalPrice;

    @OneToMany(mappedBy = "order", cascade = CascadeType.PERSIST, orphanRemoval = true)
    private Set<OrderItem> items=new LinkedHashSet<>();

    public static Order fromCart(Cart cart, User customer){
        var order=new Order();
        order.setCustomer(customer);
        order.setTotalPrice(cart.getTotalPrice());
        order.setStatus(PaymentStatus.PENDING);


        cart.getItems().forEach( item -> {
                    var orderItem = new OrderItem(order,item.getProduct(),item.getQuantity());
                    order.items.add(orderItem);
                }
        );
        return order;
    }

    public boolean isPlacedBy(User customer){
        return this.customer.equals(customer);
    }

}

