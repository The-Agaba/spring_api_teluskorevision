package com.springboot.store.mappers;

import com.springboot.store.dtos.RegisterUserRequest;
import com.springboot.store.dtos.UpdateUserRequest;
import com.springboot.store.dtos.UserDto;
import com.springboot.store.entities.User;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;


@Mapper(componentModel = "spring")
public interface UserMapper {
   UserDto toDto(User user);

   User toEntity(RegisterUserRequest request);

   void update(UpdateUserRequest request, @MappingTarget User user);
}
