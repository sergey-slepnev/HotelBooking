package com.sspdev.hotelbooking.config;

import com.sspdev.hotelbooking.database.entity.User;
import com.sspdev.hotelbooking.database.repository.UserRepository;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;

import java.io.IOException;

import static com.sspdev.hotelbooking.database.entity.enums.Role.USER;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfiguration {

    private final UserRepository userRepository;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {
        httpSecurity
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(urlConfig -> urlConfig
                        .requestMatchers(
                                "/login",
                                "/my-booking",
                                "/my-booking/registration"
                        ).permitAll()
                        .requestMatchers(
                                "/my-booking/users/{\\d+}",
                                "/my-booking/users",
                                "/my-booking/users/{\\d+}/change-status")
                        .hasAuthority(USER.getAuthority()))

                .formLogin(login -> login
                        .loginPage("/my-booking")
                        .loginProcessingUrl("/login")
                        .successHandler((request, response, authentication) -> userRepository.findByUsername(authentication.getName())
                                .ifPresent(user -> {
                                    request.getSession(true).setAttribute("authUser", user);
                                    request.getSession().getAttributeNames();
                                    redirectToUserPage(response, user);
                                })));

        return httpSecurity.build();
    }

    @SneakyThrows
    private static void redirectToUserPage(HttpServletResponse response, User user) {
        try {
            response.sendRedirect("/my-booking/users/" + user.getId());
        } catch (IOException exception) {
            throw new RuntimeException();
        }
    }
}