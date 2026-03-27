package com.springboot.store.config;

import lombok.AllArgsConstructor;
import org.jspecify.annotations.Nullable;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.SessionManagementConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.stereotype.Service;

@Configuration
@EnableWebSecurity
@AllArgsConstructor
public class SecurityConfig {

    private final UserDetailsService userDetailsService;
    @Bean
    public PasswordEncoder passwordEncoder(){

        return new BCryptPasswordEncoder();


    }

    @Bean
    public AuthenticationProvider authenticationProvider(UserDetailsService userDetailsService, PasswordEncoder passwordEncoder) {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider(userDetailsService); // pass userDetailsService here
        provider.setPasswordEncoder(passwordEncoder);
        return provider;
    }
//another possible method
//    @Bean
//    public AuthenticationProvider authenticationProvider(UserDetailsService userDetailsService, PasswordEncoder passwordEncoder) {
//        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
//        provider.setUserDetailsPasswordService(userDetailsService::loadUserByUsername); // only needed if using password upgrade
//        provider.setPasswordEncoder(passwordEncoder);
//        return provider;
//    }

    @Bean
    public AuthenticationManager authenticationManager(
            AuthenticationConfiguration config
    ) throws Exception{
        return config.getAuthenticationManager();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http)throws Exception{
        //stateless sessions
        //disable csrf
        // authorize
        http
                .sessionManagement( c->
                        c.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                        )
                .csrf(AbstractHttpConfigurer::disable
                        )
                .authenticationProvider(authenticationProvider(userDetailsService, passwordEncoder()))
                .authorizeHttpRequests(
                        c->
                                c.requestMatchers("/api/carts/**").permitAll()
                                        .requestMatchers(HttpMethod.POST,"/api/auth/login").permitAll()
                                        .requestMatchers(HttpMethod.POST,"/api/users").permitAll()
                                        .requestMatchers(HttpMethod.POST,"/api/auth/validate").permitAll()
                                        .anyRequest().authenticated()
                );
        return http.build();


    }
}
