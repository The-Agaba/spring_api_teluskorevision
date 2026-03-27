package com.springboot.store.mappers;


import com.springboot.store.dtos.ProductDto;
import com.springboot.store.entities.Product;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface ProductMapper {
    @Mapping(target = "categoryId", source = "category.id")
    ProductDto toDto(Product product);
    Product toEntity(ProductDto productDto);

    @Mapping(target = "id" , ignore = true)
    void updateProduct(ProductDto productDto, @MappingTarget Product product);
}
