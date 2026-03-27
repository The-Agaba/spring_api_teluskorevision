package com.springboot.store.controllers;


import com.springboot.store.dtos.ChangePasswordRequest;
import com.springboot.store.dtos.RegisterUserRequest;
import com.springboot.store.dtos.UpdateUserRequest;
import com.springboot.store.dtos.UserDto;
import com.springboot.store.mappers.UserMapper;
import com.springboot.store.repositories.UserRepository;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.List;
import java.util.Map;
import java.util.Set;

@RestController
@AllArgsConstructor

@RequestMapping("/api/users")
public class UserController {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;

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
    public ResponseEntity<?> registerUser(
            @Valid @RequestBody RegisterUserRequest request,
            UriComponentsBuilder builder
            ){
        if (userRepository.existsByEmail(request.getEmail())){
            return ResponseEntity.badRequest().body(
                    Map.of("email","Email already exists")
            );
        }

        var user=userMapper.toEntity(request);
        //we first encode the password before sending it
        user.setPassword(passwordEncoder.encode(user.getPassword()));
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
