package com.bluesky.player.filter;

import com.bluesky.player.database.entity.account.Account;
import com.bluesky.player.database.service.AccountService;
import com.bluesky.player.dto.account.AccountResponse;
import com.bluesky.player.util.jwt.JwtUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Component
@RequiredArgsConstructor
public class JwtAuthFilter extends OncePerRequestFilter {
    private final JwtUtil jwtUtil;
    private final AccountService accountService;//change

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        final String authHeader = request.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")){
            filterChain.doFilter(request, response);
            return;
        }
        try {
            final String jwt = authHeader.substring(7);
            final String accountEmail = jwtUtil.extractAccountEmail(jwt);

            if (accountEmail != null && SecurityContextHolder.getContext().getAuthentication() == null){
                Account account = this.accountService.getAccountByEmail(accountEmail);
                if (!jwtUtil.isTokenValid(jwt, account)) {
                    sendErrorResponse(response, "Invalid JWT token", HttpStatus.UNAUTHORIZED);
                    return;
                }

                UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                        account,
                        null,
                        getAuthorities(account)
                );
                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authToken);
            }
        } catch (Exception e){
            sendErrorResponse(response, "Invalid JWT token: " + e.getMessage(), HttpStatus.UNAUTHORIZED);
            return;
        }

        filterChain.doFilter(request, response);
    }
    //DUM method for now no need roles
    private Collection<? extends GrantedAuthority> getAuthorities(Account account) {
        List<GrantedAuthority> authorities = new ArrayList<>();
        authorities.add(new SimpleGrantedAuthority("ROLE_USER"));
        return authorities;
    }
    private void sendErrorResponse(HttpServletResponse response, String message, HttpStatus status) throws IOException {
        response.setStatus(status.value());
        response.setContentType("application/json");
        response.getWriter().write("{\"error\": \"" + message + "\"}");
    }
}
