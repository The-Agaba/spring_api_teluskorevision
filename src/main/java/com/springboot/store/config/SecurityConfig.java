package com.springboot.store.config;

import com.springboot.store.entities.Role;
import com.springboot.store.filters.JwtAuthenticationFilter;
import lombok.AllArgsConstructor;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;

import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;


@Configuration
@EnableWebSecurity
@AllArgsConstructor
public class SecurityConfig {
    private final UserDetailsService userDetailsService;
    private final JwtAuthenticationFilter jwtAuthenticationFilter;
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
                        c-> c
                                .requestMatchers("/swagger-ui/**").permitAll()
                                .requestMatchers("/swagger-ui.html").permitAll()
                                .requestMatchers("/v3/api-docs/**").permitAll()
                                .requestMatchers("/api/carts/**").permitAll()
                                .requestMatchers("/api/admin/**").hasRole(Role.ADMIN.name())
                                .requestMatchers(HttpMethod.POST,"/api/users").permitAll()
                                .requestMatchers(HttpMethod.POST,"/api/products/**").hasRole(Role.ADMIN.name())
                                .requestMatchers(HttpMethod.PUT,"/api/products/**").hasRole(Role.ADMIN.name())
                                .requestMatchers(HttpMethod.DELETE,"/api/products/**").hasRole(Role.ADMIN.name())
                                .requestMatchers(HttpMethod.GET,"/api/products/**").permitAll()
                                .requestMatchers(HttpMethod.POST,"/api/auth/login").permitAll()
                                .requestMatchers(HttpMethod.POST,"/api/auth/refresh").permitAll()
                                .requestMatchers(HttpMethod.POST,"/api/checkout/webhook").permitAll()
                                .anyRequest().authenticated()
                )
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
                .exceptionHandling(c->
                        {
                            c.authenticationEntryPoint(
                                    new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED));
                            c.accessDeniedHandler(
                                    ((request, response, accessDeniedException) ->
                                            response.setStatus(HttpStatus.FORBIDDEN.value())
                                            ));
                        }
                        );
        return http.build();


    }


}
