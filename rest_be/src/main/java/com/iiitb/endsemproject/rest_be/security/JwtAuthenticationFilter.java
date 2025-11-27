package com.iiitb.endsemproject.rest_be.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User; // Using Spring's internal User class
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtException;
import com.iiitb.endsemproject.rest_be.repository.FacultyRepository;

import java.io.IOException;
import java.util.List;

@Component // 1. Tells Spring to manage this class.
// 2. Guarantees this filter runs exactly once per HTTP request.

@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtDecoder googleJwtDecoder;
    private final FacultyRepository facultyRepository;


    // 4. This method runs on every incoming request.
    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain
    ) throws ServletException, IOException {

        // --- Step 1: Extract Token from Header ---
        final String authHeader = request.getHeader("Authorization");
        String jwt = null;
        String userEmail = null;


        if (request.getCookies() != null) {
            for (Cookie cookie : request.getCookies()) {
                if (cookie.getName().equals("FACULTY_JWT")) {
                    jwt = cookie.getValue();
//                    System.out.println("Cookie checked:"+jwt);
                    break;
                }
            }
        }
//
        //  Extract Email and Validate Token

        if (jwt == null) {
            filterChain.doFilter(request, response);
            return;
        }

        // Try Google ID token first (OIDC id_token set in FACULTY_JWT cookie)
        try {
            Jwt decoded = googleJwtDecoder.decode(jwt);
            // Basic claim extraction
            userEmail = decoded.getClaim("email");
            if (userEmail == null) {
                userEmail = decoded.getSubject();
            }
        } catch (JwtException ex) {
            // Fallback removed: if Google decode fails, we skip authentication
            filterChain.doFilter(request, response);
            return;
        }

        // Check if we have an email and the user is not ALREADY authenticated in the current session.
        if (userEmail != null && SecurityContextHolder.getContext().getAuthentication() == null) {
//
            String role=null;
            // Ensure user is an authorized faculty
            var facultyOpt = facultyRepository.findByEmail(userEmail);
            if (facultyOpt.isEmpty()) {
                filterChain.doFilter(request, response);
                return;
            } else {
                // If Faculty entity has role, prefer it
                try {
                    var faculty = facultyOpt.get();
                    role = faculty.getRole();
                } catch (Exception ignored) {}
            }

            // If role is missing/blank in DB, skip authentication
            if (role == null || role.isBlank()) {
                filterChain.doFilter(request, response);
                return;
            }

            List<GrantedAuthority> authorityList = List.of(new SimpleGrantedAuthority("ROLE_" + role));
            UserDetails userDetails = User.withUsername(userEmail).password("").roles(role).build();

            // If Google token decoded successfully, create the authentication token required by Spring Security.
            UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                    userDetails,
                    null,
                    authorityList
                    //userDetails.getAuthorities() // Authorities will be empty in this simple setup
            );

            // Add the request details (IP, session info) to the token
            authToken.setDetails(
                    new WebAuthenticationDetailsSource().buildDetails(request)
            );

            // --- Step 4: Set the Security Context ---
            // Manually set the user as authenticated for this request.
            SecurityContextHolder.getContext().setAuthentication(authToken);
           // System.out.println("Security context: "+SecurityContextHolder.getContext().getAuthentication().getName());
        }

        // Continue the request chain (e.g., to the controller)
        filterChain.doFilter(request, response);
    }
}