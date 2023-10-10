package com.sspdev.hotelbooking.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfiguration {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {
        httpSecurity
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(urlConfig -> urlConfig
                        .requestMatchers(
                                "/login",
                                "/my-booking/users",
                                "/my-booking",
                                "/my-booking/registration"
                        ).permitAll())
                .formLogin(login -> login
                        .loginPage("/my-booking")
                        .loginProcessingUrl("/login")
                        .defaultSuccessUrl("/my-booking/users"));
        return httpSecurity.build();
    }
}