package com.bluesky.player.auth;

import com.bluesky.player.database.entity.account.Account;
import com.bluesky.player.database.service.AccountService;
import com.bluesky.player.filter.JwtAuthFilter;
import com.bluesky.player.util.jwt.JwtUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.core.context.SecurityContextHolder;

import java.io.IOException;
import java.rmi.server.ServerCloneException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class JwtAuthFilterTest {
    @InjectMocks
    private JwtAuthFilter jwtAuthFilter;
    @Mock
    private JwtUtil jwtUtil;
    @Mock
    private AccountService accountService;
    @Mock
    private FilterChain filterChain;

    private MockHttpServletRequest request;
    private MockHttpServletResponse response;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setUp(){
        request = new MockHttpServletRequest();
        response = new MockHttpServletResponse();
        SecurityContextHolder.clearContext();
    }

    @Test
    void doFilter_NoAuthorizationHeader_ShouldContinueFilterChain() throws ServerCloneException, IOException, ServletException {
        jwtAuthFilter.doFilter(request,response, filterChain);

        verify(filterChain).doFilter(request, response);
        assertNull(SecurityContextHolder.getContext().getAuthentication());
    }
    @Test
    void doFilter_ValidToken_ShouldSetAuthentification() throws ServletException, IOException {
        String validToken = "valid.jwt.token";
        String email = "test@example.com";
        Account account = new Account();
        account.setEmail(email);
        request.addHeader("Authorization", "Bearer " + validToken);

        when(jwtUtil.extractAccountEmail(validToken)).thenReturn(email);
        when(accountService.getAccountByEmail(email)).thenReturn(account);
        when(jwtUtil.isTokenValid(validToken, account)).thenReturn(true);

        jwtAuthFilter.doFilter(request, response, filterChain);
        verify(filterChain).doFilter(request,response);
        var authentication = SecurityContextHolder.getContext().getAuthentication();
        assertNotNull(authentication);
        assertEquals(account, authentication.getPrincipal());
        assertTrue(authentication.getAuthorities().stream()
                .anyMatch(auth -> auth.getAuthority().equals("ROLE_USER")));
    }
}
