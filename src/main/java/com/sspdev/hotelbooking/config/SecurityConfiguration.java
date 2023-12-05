package com.sspdev.hotelbooking.config;

import com.sspdev.hotelbooking.database.entity.User;
import com.sspdev.hotelbooking.database.repository.UserRepository;
import com.sspdev.hotelbooking.service.UserService;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.oidc.user.DefaultOidcUser;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.security.web.DefaultSecurityFilterChain;
import org.springframework.security.web.savedrequest.NullRequestCache;

import java.io.IOException;
import java.lang.reflect.Proxy;
import java.util.Set;

import static com.sspdev.hotelbooking.database.entity.enums.Role.ADMIN;
import static com.sspdev.hotelbooking.database.entity.enums.Role.OWNER;
import static com.sspdev.hotelbooking.database.entity.enums.Role.USER;

@RequiredArgsConstructor
@Configuration
@EnableWebSecurity
public class SecurityConfiguration {

    private final UserRepository userRepository;
    private final UserService userService;

    @Bean
    public DefaultSecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {
        var requestCache = new NullRequestCache();
        httpSecurity
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(urlConfig -> urlConfig
                        .requestMatchers(
                                "/my-booking/rooms",
                                "/login",
                                "/my-booking",
                                "/my-booking/registration",
                                "/my-booking/users/create",
                                "/my-booking/hotels",
                                "/my-booking/hotels/{\\d+}",
                                "/api/v1/hotels/{\\d+}/content/{d\\+}",
                                "/my-booking/rooms",
                                "/my-booking/rooms/{\\d+}",
                                "/api/v1/rooms/{\\d+}/content/{d\\+}",
                                "/my-booking/hotels",
                                "/my-booking/hotels/{\\d+}",
                                "/api/v1/hotels/{\\d+}/content/{d\\+}",
                                "/my-booking/rooms/{d\\+}/rooms-by-hotel"
                        ).permitAll()
                        .requestMatchers(
                                "/my-booking/users/{\\d+}",
                                "/api/v1/users/{\\d+}/avatar",
                                "/my-booking/users",
                                "/my-booking/users/{\\d+}/update",
                                "/my-booking/users/{\\d+}/delete",
                                "/my-booking/booking-requests/{\\d+}"
                        ).hasAnyAuthority(USER.getAuthority(), OWNER.getAuthority(), ADMIN.getAuthority())
                        .requestMatchers(
                                "/my-booking/hotels/{d\\+}/add-hotel",
                                "/my-booking/hotels/{d\\+}/create",
                                "/my-booking/hotels/{d\\+}/delete",
                                "/my-booking/hotels/{\\d+}/hotels-by-user",
                                "/my-booking/hotels/{d\\+}/user-hotels/{\\d+}/edit",
                                "/my-booking/rooms/{\\d+}/{\\d+}/add",
                                "/my-booking/rooms/{\\d+}/{\\d+}/create",
                                "/api/v1/rooms/content/{d\\+}/delete",
                                "/my-booking/rooms/{d\\+}/delete",
                                "/my-booking/rooms/{d\\+}/update",
                                "/my-booking/rooms/{d\\+}/edit",
                                "/my-booking/hotels/{\\d+}/hotels-by-user"
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
                .oauth2Login(provider -> provider
                        .loginPage("/login")
                        .defaultSuccessUrl("/my-booking/users")
                        .userInfoEndpoint(userInfo -> userInfo.oidcUserService(oidcUserService())))

                .logout(logout -> logout
                        .logoutUrl("/logout").permitAll()
                        .logoutSuccessUrl("/my-booking")
                        .clearAuthentication(true)
                        .invalidateHttpSession(true)
                        .deleteCookies("JSESSIONID"));

        return httpSecurity.build();
    }

    private OAuth2UserService<OidcUserRequest, OidcUser> oidcUserService() {
        return userRequest -> {
            String email = userRequest.getIdToken().getClaim("email");
            var userDetails = userService.loadUserByUsername(email);
            var methods = Set.of(userDetails.getClass().getMethods());
            var oidcUser = new DefaultOidcUser(userDetails.getAuthorities(), userRequest.getIdToken());
            return (OidcUser) Proxy.newProxyInstance(SecurityConfiguration.class.getClassLoader(),
                    new Class[]{UserDetails.class, OidcUser.class},
                    (proxy, method, args) -> methods.contains(method)
                            ? method.invoke(userDetails, args)
                            : method.invoke(oidcUser, args));
        };
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