package com.springboot.store.dtos;

import com.springboot.store.entities.Product;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

/**
 * DTO for {@link Product}
 */
@Data
public class CartProductDto {
    @NotNull
    private Long id;
    private String name;
    private BigDecimal price;
}