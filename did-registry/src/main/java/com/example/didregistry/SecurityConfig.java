package com.example.didregistry;

import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jose.jwk.source.ImmutableJWKSet;
import com.nimbusds.jose.jwk.source.JWKSource;
import com.nimbusds.jose.proc.SecurityContext;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.oidc.OidcScopes;
import org.springframework.security.oauth2.server.authorization.config.annotation.web.configuration.OAuth2AuthorizationServerConfiguration;
import org.springframework.security.oauth2.server.authorization.client.InMemoryRegisteredClientRepository;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClient;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClientRepository;
import org.springframework.security.oauth2.server.authorization.config.annotation.web.configurers.OAuth2AuthorizationServerConfigurer;
import org.springframework.security.oauth2.server.authorization.settings.AuthorizationServerSettings;
import org.springframework.security.oauth2.server.authorization.settings.ClientSettings;
import org.springframework.security.oauth2.server.authorization.token.JwtEncodingContext;
import org.springframework.security.oauth2.server.authorization.token.OAuth2TokenCustomizer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AnonymousAuthenticationFilter;
import org.springframework.security.web.authentication.LoginUrlAuthenticationEntryPoint;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationToken;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.interfaces.RSAPublicKey;
import java.util.List;
import java.util.UUID;

@Configuration
public class SecurityConfig {

    @Bean
    @Order(1)
    SecurityFilterChain asChain(HttpSecurity http) throws Exception {

        OAuth2AuthorizationServerConfiguration.applyDefaultSecurity(http);

        http.getConfigurer(OAuth2AuthorizationServerConfigurer.class)
                .oidc(Customizer.withDefaults());

        http.exceptionHandling(ex ->
                ex.authenticationEntryPoint(new LoginUrlAuthenticationEntryPoint("/login"))
        );

        http.addFilterBefore((request, response, chain) -> {
            if (request instanceof HttpServletRequest httpReq) {
                HttpSession session = httpReq.getSession(false);
                if (session != null) {
                    Object didObj = session.getAttribute("AUTHENTICATED_DID");
                    if (didObj instanceof String did && !did.isBlank()) {

                        AbstractAuthenticationToken auth =
                                new PreAuthenticatedAuthenticationToken(
                                        did,
                                        "N/A",
                                        List.<GrantedAuthority>of()
                                );
                        auth.setAuthenticated(true);

                        org.springframework.security.core.context.SecurityContextHolder
                                .getContext()
                                .setAuthentication(auth);
                    }
                }
            }
            chain.doFilter(request, response);
        }, AnonymousAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    @Order(2)
    SecurityFilterChain uiChain(HttpSecurity http) throws Exception {

        http
                .authorizeHttpRequests(a -> a
                        .requestMatchers("/", "/login", "/error", "/error/**", "/css/**").permitAll()
                        .anyRequest().authenticated()
                )
                .csrf(csrf -> csrf.ignoringRequestMatchers("/login"));

        return http.build();
    }

    /**
     * Register "demo-service" as an OAuth2 client with this AS.
     * Disable consent to avoid rendering a consent screen.
     */
    @Bean
    RegisteredClientRepository registeredClientRepository() {
        RegisteredClient demoClient = RegisteredClient.withId(UUID.randomUUID().toString())
                .clientId("demo-service")
                .clientSecret("{noop}demo-secret")
                .authorizationGrantType(AuthorizationGrantType.AUTHORIZATION_CODE)
                .authorizationGrantType(AuthorizationGrantType.REFRESH_TOKEN)
                .redirectUri("http://localhost:8080/login/oauth2/code/did")
                .scope(OidcScopes.OPENID)
                .scope("profile")
                .scope("connector.read")
                .clientSettings(
                        ClientSettings.builder()
                                .requireAuthorizationConsent(false)
                                .build()
                )
                .build();

        return new InMemoryRegisteredClientRepository(demoClient);
    }

    /**
     * Issuer URL etc. Must match what the demo-service config uses
     * for the "did" provider (issuer-uri or provider URIs).
     */
    @Bean
    AuthorizationServerSettings authorizationServerSettings() {
        return AuthorizationServerSettings.builder()
                .issuer("http://localhost:9100")
                .build();
    }

    /**
     * JWK source for signing ID tokens and access tokens.
     */
    @Bean
    JWKSource<SecurityContext> jwkSource() {
        try {
            KeyPairGenerator kpg = KeyPairGenerator.getInstance("RSA");
            kpg.initialize(2048);
            KeyPair kp = kpg.generateKeyPair();

            RSAKey rsaKey = new RSAKey.Builder((RSAPublicKey) kp.getPublic())
                    .privateKey(kp.getPrivate())
                    .keyID(UUID.randomUUID().toString())
                    .build();

            return new ImmutableJWKSet<>(new JWKSet(rsaKey));
        } catch (Exception e) {
            throw new IllegalStateException("Failed to initialize JWK source", e);
        }
    }

    /**
     * ID Token customization.
     * We pull the DID (principalName) and also attach connector_endpoint.
     *
     * NOTE: We use ctx.getAuthorization().getPrincipalName() first for safety,
     * and fall back to ctx.getPrincipal() if needed.
     */
    @Bean
    OAuth2TokenCustomizer<JwtEncodingContext> tokenCustomizer(UsersConfig users) {
        return ctx -> {
            if (!"id_token".equals(ctx.getTokenType().getValue())) {
                return;
            }

            String did = null;
            if (ctx.getAuthorization() != null) {
                did = ctx.getAuthorization().getPrincipalName();
            }
            if (did == null && ctx.getPrincipal() != null) {
                did = ctx.getPrincipal().getName();
            }
            if (did == null || did.isBlank()) {
                return;
            }

            ctx.getClaims().subject(did);
            ctx.getClaims().claim(
                    "connector_endpoint",
                    users.connectorEndpointForDid(did)
            );
        };
    }
}