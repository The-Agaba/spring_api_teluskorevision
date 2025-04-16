package com.codewithmosh.store.controllers;


import com.codewithmosh.store.dtos.ChangePasswordRequest;
import com.codewithmosh.store.dtos.RegisterUserRequest;
import com.codewithmosh.store.dtos.UpdateUserRequest;
import com.codewithmosh.store.dtos.UserDto;
import com.codewithmosh.store.mappers.UserMapper;
import com.codewithmosh.store.repositories.UserRepository;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.List;
import java.util.Set;

@RestController
@AllArgsConstructor

@RequestMapping("api/users")
public class UserController {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    @GetMapping
    public List<UserDto> getAllUsers(
            //@RequestHeader(required = false, name="x-authtoken") String authToken,
            @RequestParam(required = false, defaultValue = "", name = "sort") String sortBy
    ){
        if(!Set.of("name","email").contains(sortBy))
            sortBy ="name";

        return  userRepository.findAll(Sort.by(sortBy))
                .stream()
                .map(userMapper::toDto
                ).toList();
    }
    @GetMapping("/{id}")
    public ResponseEntity<UserDto> getUser(@PathVariable Long id){
        var user= userRepository.findById(id).orElse(null);
        if (user==null){
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(userMapper.toDto(user));
    }

    @PostMapping
    public ResponseEntity<UserDto> createUser(
            @Valid @RequestBody RegisterUserRequest request, UriComponentsBuilder builder
            ){
        var user=userMapper.toEntity(request);
        userRepository.save(user);

        var userDto=userMapper.toDto(user);
     return ResponseEntity.created(builder.path("/api/users/{id}").buildAndExpand(userDto.getId()).toUri()).body(userDto);
    }

    @PutMapping("{id}")
    public ResponseEntity<UserDto> updateUser(
            @PathVariable Long id,
            @RequestBody UpdateUserRequest request){
        var  user=userRepository.findById(id).orElse(null);
        if (user==null){
            return ResponseEntity.notFound().build();
        }
         userMapper.update(request,user);
        userRepository.save(user);
        return ResponseEntity.ok(userMapper.toDto(user));
    }

    @DeleteMapping("{id}")
    public ResponseEntity<UserDto> deleteUser(@PathVariable Long id){
        var user=userRepository.findById(id).orElse(null);

        if (user==null){
            return ResponseEntity.notFound().build();
        }
        userRepository.delete(user);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("{id}/change-password")
    public ResponseEntity<Void> changePassword(
            @PathVariable Long id,
            @RequestBody ChangePasswordRequest request
            ){
        var  user=userRepository.findById(id).orElse(null);
        if (user==null){
            return ResponseEntity.notFound().build();
        }

        if(!user.getPassword().equals(request.getOldPassword())){
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }

        user.setPassword(request.getNewPassword());
        userRepository.save(user);
        return ResponseEntity.noContent().build();

    }


}
