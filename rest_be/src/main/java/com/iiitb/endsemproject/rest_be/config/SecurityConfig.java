package com.iiitb.endsemproject.rest_be.config;


import com.iiitb.endsemproject.rest_be.security.JwtAuthenticationFilter;
import com.iiitb.endsemproject.rest_be.security.OAuth2AuthenticationSuccessHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import lombok.RequiredArgsConstructor;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.client.web.DefaultOAuth2AuthorizationRequestResolver;
import org.springframework.security.oauth2.client.web.OAuth2AuthorizationRequestResolver;
import org.springframework.security.oauth2.core.endpoint.OAuth2AuthorizationRequest;
import jakarta.servlet.http.HttpServletRequest;
 import org.springframework.beans.factory.annotation.Value;
 import org.springframework.security.oauth2.jwt.Jwt;
 import org.springframework.security.oauth2.jwt.JwtDecoder;
 import org.springframework.security.oauth2.jwt.JwtValidators;
 import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
 import org.springframework.security.oauth2.core.DelegatingOAuth2TokenValidator;
 import org.springframework.security.oauth2.core.OAuth2TokenValidator;

import java.util.Arrays;
import java.util.List;
import java.util.HashMap;
import java.util.Map;
@RequiredArgsConstructor
@Configuration // 1. Marks this as a configuration class for Spring.
@EnableWebSecurity // 2. Activates Spring Security features.
public class SecurityConfig {

    private final OAuth2AuthenticationSuccessHandler oauth2SuccessHandler;
    private final ClientRegistrationRepository clientRegistrationRepository;


    // 4. This is the main configuration method for HTTP security.
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http, JwtAuthenticationFilter jwtAuthFilter) throws Exception {

        http
                // --- A. Configure CORS ---
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))

                // --- B. Disable CSRF ---
                // CSRF is not needed for stateless REST APIs.
                .csrf(AbstractHttpConfigurer::disable)

                // --- C. Configure URL Access Rules ---
                .authorizeHttpRequests(auth -> auth
                        // Allow these endpoints to be accessed without authentication (login flow)
                        .requestMatchers("/oauth2/**", "/login/**").permitAll()

                        // All /api/** requests must be authenticated (single user type)
                        .requestMatchers("/api/**").authenticated()
                        .anyRequest().permitAll() // Allow everything else (e.g., static files)
                )

                // --- D. Configure OAuth2 Login ---
                .oauth2Login(oauth2 -> oauth2
                .authorizationEndpoint(authorization ->
                        authorization.baseUri("/oauth2/authorization").authorizationRequestResolver(oauth2AuthorizationRequestResolver())
                )
                .redirectionEndpoint(redirection ->
                        redirection.baseUri("/oauth2/callback/*")
                )
                .successHandler(oauth2SuccessHandler)
        )


                // --- E. Configure Session Management (Stateless) ---
                .sessionManagement(sess -> sess
                        // Crucial: Set the application to be STATELESS (no session/cookies)
                        // All security is handled by the JWT in the header.
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )

                // --- F. Add Our JWT Filter ---
                // Tell Spring to run our custom JWT filter BEFORE its standard filter.
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    /**
     * This bean is responsible for customizing the OAuth2 authorization request by adding the "prompt=login"
     * parameter. This is necessary for the Google OAuth2 flow to work correctly.
     *
     * The bean wraps the default resolver and overrides the {@link OAuth2AuthorizationRequestResolver#resolve(HttpServletRequest)}
     * and {@link OAuth2AuthorizationRequestResolver#resolve(HttpServletRequest, String)} methods to add the "prompt=login"
     * parameter to the authorization request.
     *
     * @return a new instance of the OAuth2 authorization request resolver
     */
    @Bean
    public OAuth2AuthorizationRequestResolver oauth2AuthorizationRequestResolver() {
        DefaultOAuth2AuthorizationRequestResolver defaultResolver =
                new DefaultOAuth2AuthorizationRequestResolver(clientRegistrationRepository, "/oauth2/authorization");
        return new OAuth2AuthorizationRequestResolver() {
            @Override
            public OAuth2AuthorizationRequest resolve(HttpServletRequest request) {
                OAuth2AuthorizationRequest req = defaultResolver.resolve(request);
                return customize(req);
            }

            /**
             * Customizes the OAuth2 authorization request by adding the "prompt=login"
             * parameter. This is necessary for the Google OAuth2 flow to work correctly.
             *
             * @param request the HTTP request
             * @param clientRegistrationId the client registration ID
             * @return the customized OAuth2 authorization request
             */
            @Override
            public OAuth2AuthorizationRequest resolve(HttpServletRequest request, String clientRegistrationId) {
                OAuth2AuthorizationRequest req = defaultResolver.resolve(request, clientRegistrationId);
                return customize(req);
            }

            /**
             * Customizes the OAuth2 authorization request by adding the "prompt=login"
             * parameter. This is necessary for the Google OAuth2 flow to work correctly.
             *
             * @param req the OAuth2 authorization request to customize
             * @return the customized OAuth2 authorization request
             */
            private OAuth2AuthorizationRequest customize(OAuth2AuthorizationRequest req) {
                if (req == null) return null;
                Map<String, Object> additional = new HashMap<>(req.getAdditionalParameters());
                additional.put("prompt", "login");
                return OAuth2AuthorizationRequest.from(req)
                        .additionalParameters(additional)
                        .build();
            }
        };
    }

    // 5. CORS Configuration Bean
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();

        // IMPORTANT: Allow your frontend domain here.
        configuration.setAllowedOrigins(List.of("http://localhost:5173","http://localhost:8080"));

        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(List.of("*"));
        configuration.setAllowCredentials(true); // Needed for OAuth redirect cookies (briefly)

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration); // Apply this config to all paths
        return source;
    }

    /**
     * Configures a NimbusJwtDecoder with the public Google OAuth2 JWKS set and validates the JWT
     * with the given client ID.
     *
     * @param clientId the client ID of the Google OAuth2 client
     * @return the configured NimbusJwtDecoder
     */
    @Bean
    public JwtDecoder googleJwtDecoder(
            @Value("${spring.security.oauth2.client.registration.google.client-id}") String clientId
    ) {
        NimbusJwtDecoder decoder = NimbusJwtDecoder.withJwkSetUri("https://www.googleapis.com/oauth2/v3/certs").build();
        OAuth2TokenValidator<Jwt> withIssuer = JwtValidators.createDefaultWithIssuer("https://accounts.google.com");
        OAuth2TokenValidator<Jwt> withAudience = new com.iiitb.endsemproject.rest_be.security.AudienceValidator(clientId);
        OAuth2TokenValidator<Jwt> validator = new DelegatingOAuth2TokenValidator<>(withIssuer, withAudience);
        decoder.setJwtValidator(validator);
        return decoder;
    }
}
