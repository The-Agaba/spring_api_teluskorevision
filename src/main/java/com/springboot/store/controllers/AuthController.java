package com.springboot.store.controllers;

import com.springboot.store.config.JwtConfig;
import com.springboot.store.dtos.JwtResponse;
import com.springboot.store.dtos.LoginRequest;
import com.springboot.store.dtos.UserDto;
import com.springboot.store.mappers.UserMapper;
import com.springboot.store.repositories.UserRepository;
import com.springboot.store.services.JwtService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@AllArgsConstructor
@RequestMapping("api/auth")
public class AuthController {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final JwtConfig jwtConfig;
    private final UserMapper userMapper;


    @PostMapping("/login")
    public ResponseEntity<JwtResponse> login(
            @Valid @RequestBody LoginRequest request,
            HttpServletResponse response
    ){

        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                request.getEmail(),
                request.getPassword()
                )
        );

        //before using the proper authentication manager interface
//        var user=userRepository.findByEmail(request.getEmail()).orElse(null);
//        if(user==null){
//            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
//        }
//
//        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
//            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
//        }
        var user=userRepository.findByEmail(request.getEmail()).orElseThrow();

        var refreshToken= jwtService.generateRefreshToken(user);
        var accessToken=jwtService.generateAccessToken(user);
        var cookie=new Cookie("refreshToken", refreshToken.toString());
        cookie.setHttpOnly(true);
        cookie.setPath("/auth/refresh");
        cookie.setMaxAge(jwtConfig.getRefreshTokenExpiration());
        cookie.setSecure(true);
        response.addCookie(cookie);

        return ResponseEntity.ok(new JwtResponse(accessToken.toString()));
    }
//removed becoz we don't need it anymore
//    @PostMapping("/validate")
//   public boolean validate(@RequestHeader("Authorization") String authHeader){
//        System.out.println("Validate called");
//        var token=authHeader.replace("Bearer ","");
//     return jwtService.validateToken(token);
//   }

    @PostMapping("/refresh")
    public ResponseEntity<JwtResponse> refresh(
        @CookieValue(value = "refreshToken") String refreshToken){
        var jwt=jwtService.parseToken(refreshToken);

        if(jwt==null||jwt.isExpired()){
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        var user=userRepository.findById(jwt.getUserId()).orElseThrow();
        var accessToken=jwtService.generateAccessToken(user);
        return ResponseEntity.ok(new JwtResponse(accessToken.toString()));
    }

    @GetMapping("/me")
    public ResponseEntity<UserDto> me(){
        var authentication= SecurityContextHolder.getContext().getAuthentication();
        var useId=(Long) authentication.getPrincipal();

        //
        var user=userRepository.findById(useId).orElse(null);

        if (user == null ){
            return ResponseEntity.notFound().build();
        }
        var userDto=userMapper.toDto(user);

        return ResponseEntity.ok(userDto);

    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<Void> handleBadCredentialsException(){
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }


}
