// demo-service/src/main/java/com/example/demoservice/SecurityConfig.java
package com.example.demoservice;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserRequest;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserService;
import org.springframework.security.oauth2.core.oidc.OidcIdToken;
import org.springframework.security.oauth2.core.oidc.user.DefaultOidcUser;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.security.web.SecurityFilterChain;

import java.util.Collections;

@Configuration
public class SecurityConfig {

    /** An OIDC user service that uses only the ID token (no /userinfo call). */
    @Bean
    OidcUserService idTokenOnlyUserService() {
        return new OidcUserService() {
            @Override
            public OidcUser loadUser(OidcUserRequest userRequest) {
                // DO NOT call super.loadUser(userRequest); that would hit /userinfo
                OidcIdToken idToken = userRequest.getIdToken();
                // Build a user just from the ID token claims
                return new DefaultOidcUser(Collections.emptyList(), idToken);
            }
        };
    }

    @Bean
    SecurityFilterChain appSecurity(HttpSecurity http, OidcUserService idTokenOnlyUserService) throws Exception {
        http
                .authorizeHttpRequests(a -> a
                        .requestMatchers("/", "/oauth2/**", "/login/oauth2/**").permitAll()
                        .anyRequest().authenticated()
                )
                .oauth2Login(o -> o
                        .userInfoEndpoint(ui -> ui.oidcUserService(idTokenOnlyUserService)) // <-- key line
                        .defaultSuccessUrl("/profile", true)
                )
                .logout(l -> l.logoutSuccessUrl("/").permitAll());
        return http.build();
    }
}