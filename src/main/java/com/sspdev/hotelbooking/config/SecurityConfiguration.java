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
import org.springframework.security.web.DefaultSecurityFilterChain;
import org.springframework.security.web.savedrequest.NullRequestCache;

import java.io.IOException;

import static com.sspdev.hotelbooking.database.entity.enums.Role.*;

@RequiredArgsConstructor
@Configuration
@EnableWebSecurity
public class SecurityConfiguration {

    private final UserRepository userRepository;

    @Bean
    public DefaultSecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {
        var requestCache = new NullRequestCache();
        httpSecurity
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(urlConfig -> urlConfig
                        .requestMatchers(
                                "/my-booking/rooms/search",
                                "/login",
                                "/my-booking",
                                "/my-booking/registration",
                                "/my-booking/users/create",
                                "/my-booking/rooms",
                                "/my-booking/rooms/{\\d+}",
                                "/api/v1/rooms/{\\d+}/content/{d\\+}"
                        ).permitAll()
                        .requestMatchers(
                                "/my-booking/users/{\\d+}",
                                "/api/v1/users/{\\d+}/avatar",
                                "/my-booking/users",
                                "/my-booking/users/{\\d+}/update",
                                "/my-booking/users/{\\d+}/delete"
                        ).hasAnyAuthority(USER.getAuthority(), OWNER.getAuthority(), ADMIN.getAuthority())
                        .requestMatchers(
                                "/my-booking/rooms/{\\d+}/{\\d+}/add",
                                "/my-booking/rooms/{\\d+}/{\\d+}/create"
                        ).hasAuthority(OWNER.getAuthority())
                        .requestMatchers(
                                "/my-booking/users/{\\d+}/change-status"
                        ).hasAuthority(ADMIN.getAuthority()))

                .requestCache(cache -> cache.requestCache(requestCache))

                .formLogin(login -> login
                        .loginPage("/my-booking").permitAll()
                        .loginProcessingUrl("/login").permitAll()
                        .successHandler((request, response, authentication) -> userRepository.findByUsername(authentication.getName())
                                .ifPresent(user -> {
                                    request.getSession(true).setAttribute("authUser", user);
                                    request.getSession().getAttributeNames();
                                    redirectToUserPage(response, user);
                                })))

                .logout(logout -> logout
                        .logoutUrl("/logout").permitAll()
                        .logoutSuccessUrl("/my-booking")
                        .clearAuthentication(true)
                        .invalidateHttpSession(true)
                        .deleteCookies("JSESSIONID"));

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