package com.example.demo.security;

import com.example.demo.exceptions.TokenExpiredException;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.preauth.AbstractPreAuthenticatedProcessingFilter;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.servlet.HandlerExceptionResolver;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@Component
public class JwtFilter extends OncePerRequestFilter {

    @Autowired
    private JWTUtil jwtUtil;
    private final HandlerExceptionResolver resolver;
    @Autowired
    public JwtFilter(@Qualifier("handlerExceptionResolver") HandlerExceptionResolver resolver) {
        this.resolver = resolver;
    }
    @Override
    // Why are we using HandlerExceptionResolver here?
    // The JwtFilter runs before the @ControllerAdvice, so exceptions from the filter won't be caught by our GlobalExceptionHandler.
    // The resolver acts as a bridge, forwarding the exception from the filter layer to the main Spring exception handling mechanism.
    // resolver.resolveException(request, response, null, e);

    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        String authorizationHeader = request.getHeader("Authorization");
        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            String token = authorizationHeader.substring(7);
            try {
                jwtUtil.ValidateToken(token);
                String username = jwtUtil.extractUsername(token);
                Map<String, Object> claims = jwtUtil.extractClaims(token);

                List<String> authorities = (List<String>) claims.get("permissions");
                if(claims.containsKey("roles")) {
                    authorities.addAll((List<String>) claims.get("roles"));
                }

                UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(username,null, AuthorityUtils.createAuthorityList(authorities.toArray(new String[0])));

                SecurityContextHolder.getContext().setAuthentication(authentication);
            } catch (JwtException e) {
                resolver.resolveException(request, response, null, e);
                return;
            }
        }
        filterChain.doFilter(request,response);
    }
}