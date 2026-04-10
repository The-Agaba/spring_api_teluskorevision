package com.springboot.store.services;

import com.springboot.store.entities.User;
import com.springboot.store.repositories.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class AuthService {
    private final UserRepository userRepository;

    public User getCurrentUSer(){
        var authentication = SecurityContextHolder.getContext().getAuthentication();
        var userId=(Long) authentication.getPrincipal();

        return userRepository.findById(userId).orElse(null);
    }
}
