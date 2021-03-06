package com.epam.nazar.grinko.securities.jwt;

import com.epam.nazar.grinko.exceptions.JwtAuthenticationException;
import lombok.AllArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.GenericFilterBean;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
@AllArgsConstructor
public class JwtTokenFilter extends GenericFilterBean {
    private final JwtTokenProvider jwtTokenProvider;

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        String token = jwtTokenProvider.resolveToken((HttpServletRequest) servletRequest);
        try {
            if (token != null && jwtTokenProvider.validateToken(token)) {
                Authentication authentication = null;
                try{
                    authentication = jwtTokenProvider.getAuthentication(token);
                }catch (UsernameNotFoundException e){
                    jwtTokenProvider.removeCookieToken((HttpServletResponse) servletResponse);
                }
                if (authentication != null) {
                    // Устанавливает принципала в контекст нового принципала.
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                }
            }
        } catch (JwtAuthenticationException e) {
            SecurityContextHolder.clearContext();
            HttpServletResponse response = (HttpServletResponse) servletResponse;
            response.sendError(e.getHttpStatus().value());

            throw new JwtAuthenticationException("JWT token is expired or invalid");
        }
        filterChain.doFilter(servletRequest, servletResponse);
    }
}
