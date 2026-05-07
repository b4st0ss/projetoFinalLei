package com.example.app.security;

import com.auth0.jwt.interfaces.DecodedJWT;
import com.example.app.entity.User;
import com.example.app.service.TokenService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;

@Component
public class SecurityFilter  extends OncePerRequestFilter {

    @Autowired
    TokenService tokenService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        var token = this.recoverToken(request);

        if (token != null){
            DecodedJWT decodedToken = tokenService.tokenValidation(token);

            if (decodedToken != null){
                Long user_Id = Long.parseLong(decodedToken.getSubject());
                String role = decodedToken.getClaim("role").asString();

                role = "ROLE_" + role;

                User user = new User();
                user.setId(user_Id);
                user.setRole(role);

                var authorities = Collections.singletonList(new SimpleGrantedAuthority(role));
                var authentication = new UsernamePasswordAuthenticationToken(user, null, authorities);
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
        }
        filterChain.doFilter(request,response);
    }

    public String recoverToken(HttpServletRequest request){
        var authHeader = request.getHeader("Authorization");
        if (authHeader == null) return null;
        return  authHeader.replace("Bearer ", "");
    }
}
