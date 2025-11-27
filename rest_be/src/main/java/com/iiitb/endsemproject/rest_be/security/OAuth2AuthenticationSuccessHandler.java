package com.iiitb.endsemproject.rest_be.security;

import com.iiitb.endsemproject.rest_be.entity.Faculty;
import com.iiitb.endsemproject.rest_be.repository.FacultyRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.web.util.UriComponentsBuilder;
import org.springframework.http.ResponseCookie;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.Duration;

// 1. Tells Spring to manage this class.
// 2. Extends Spring's built-in success handler to provide custom redirect logic.
@RequiredArgsConstructor
@Service
public class OAuth2AuthenticationSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final FacultyRepository facultyRepository;
    private final OAuth2AuthorizedClientService authorizedClientService;
    String targetUrl;

    // 3. Injecting dependencies (Services and Repositories).
    // Spring automatically finds and provides these instances.

    // 4. Injecting the frontend URI where the token needs to be sent.
    @Value("${app.oauth2.authorized-redirect-uri}")
    private String redirectUri;

    // 5. This method is called by Spring Security upon successful login.
    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException {
        OAuth2AuthenticationToken oauth2Token = (OAuth2AuthenticationToken) authentication;
        OAuth2AuthorizedClient client = authorizedClientService.loadAuthorizedClient(
                oauth2Token.getAuthorizedClientRegistrationId(),
                oauth2Token.getName()
        );

        // 2. Get the ID Token (our new cookie value)
        if (!(authentication.getPrincipal() instanceof OidcUser oidcUser)) {
            // If not OIDC, we cannot produce an ID token cookie used by the rest of the flow
            throw new AccessDeniedException("OIDC id_token not available. Ensure 'openid' scope is configured.");
        }
        String idTokenValue = oidcUser.getIdToken().getTokenValue();
        String accessTokenValue = client != null && client.getAccessToken() != null ? client.getAccessToken().getTokenValue() : null;
        // --- Step 1: Get User Details from Google's Response ---
        // The Authentication object contains the user information provided by Google.
        String email = oidcUser.getAttribute("email");
        String name = oidcUser.getAttribute("name");
        String photoUrl = oidcUser.getAttribute("picture"); // We added this field earlier!

        try {
            Faculty faculty = facultyRepository.findByEmail(email).orElseThrow(() -> new AccessDeniedException("User not registered as an authorized faculty member."));

            // Update user information and save it (or create new user)
//        faculty.setEmail(email);
//        faculty.setName(name);
            faculty.setProfilePictureUrl(photoUrl);
            // Role is set as "FACULTY" in the Faculty entity default constructor, which is fine.

            facultyRepository.save(faculty); // Save the updated/new user to MySQL

            ResponseCookie cookie = ResponseCookie.from("FACULTY_JWT", idTokenValue)
                    .httpOnly(true)
                    .secure(false)   // IMPORTANT
                    .path("/")
                    .maxAge(Duration.ofSeconds(60 * 60 * 24))
                    .sameSite("Lax")   // Also IMPORTANT
                    .build();
            System.out.println("Cookie set");

            response.addHeader("Set-Cookie", cookie.toString());
            if (accessTokenValue != null) {
                ResponseCookie accessCookie = ResponseCookie.from("GOOGLE_ACCESS_TOKEN", accessTokenValue)
                        .httpOnly(true)
                        .secure(false)
                        .path("/")
                        .maxAge(Duration.ofSeconds(60 * 60))
                        .sameSite("Lax")
                        .build();
                response.addHeader("Set-Cookie", accessCookie.toString());
            }


            // Redirect to Frontend with the JWT

            targetUrl = UriComponentsBuilder.fromUriString(redirectUri)
                    .queryParam("auth_status","success")
                    .build().toUriString();

            // Use the inherited method to perform the actual browser redirect.
            getRedirectStrategy().sendRedirect(request, response, targetUrl);
        } catch (AccessDeniedException e) {
            ResponseCookie cookie = ResponseCookie.from("FACULTY_JWT","")
                    .httpOnly(true)
                    .secure(false)   // IMPORTANT
                    .path("/")
                    .maxAge(0)
                    .sameSite("Lax")   // Also IMPORTANT
                    .build();
            response.addHeader("Set-Cookie", cookie.toString());
            targetUrl = UriComponentsBuilder.fromUriString(redirectUri)
                    .queryParam("error", "User not registered as an authorized faculty member.")
                    .build().toUriString();
            getRedirectStrategy().sendRedirect(request, response, targetUrl);
        }
    }
}