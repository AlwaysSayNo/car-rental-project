package com.epam.nazar.grinko.securities.jwt;

import com.epam.nazar.grinko.domians.User;
import com.epam.nazar.grinko.exceptions.JwtAuthenticationException;
import com.epam.nazar.grinko.utils.Utility;
import io.jsonwebtoken.*;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Base64;
import java.util.Date;
import java.util.Objects;

@Component
@PropertySource("classpath:META-INF/my-application.properties")
public class JwtTokenProvider {

    private final UserDetailsService userDetailsService;

    @Value("${jwt.secret.key}")
    private String secretKey;
    @Value("${jwt.cookie.name}")
    private String authorizationHeader;
    @Value("${jwt.expiration}")
    private long validityInMilliseconds;

    public JwtTokenProvider(@Qualifier("userDetailsServiceImpl") UserDetailsService userDetailsService) {
        this.userDetailsService = userDetailsService;
    }

    @PostConstruct
    protected void init() {
        secretKey = Base64.getEncoder().encodeToString(secretKey.getBytes());
    }

    public String createToken(String username, String role) {
        Claims claims = Jwts.claims().setSubject(username);
        claims.put("role", role);
        Date now = new Date();
        Date validity = new Date(now.getTime() + validityInMilliseconds * 1000);

        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(now)
                .setExpiration(validity)
                .signWith(SignatureAlgorithm.HS256, secretKey)
                .compact();
    }

    public Cookie createCookie(String token){
        Cookie tokenCookie = new Cookie(authorizationHeader, token);
        int maxAge = (int) (new Date().getTime() - getExpiration(token).getTime());
        tokenCookie.setMaxAge(maxAge);
        return tokenCookie;
    }

    // Проверяем токен на валидность формату JWT
    // Если токен валидный, то проверяем не истек ли его срок.
    public boolean validateToken(String token) {
        try {
            Jws<Claims> claimsJws = Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token);
            return !claimsJws.getBody().getExpiration().before(new Date());
        } catch (JwtException | IllegalArgumentException e) {
            throw new JwtAuthenticationException("JWT token is expired or invalid", HttpStatus.UNAUTHORIZED);
        }
    }

    // Из ДАО достает UserDetails и возвразает Authentication.
    public Authentication getAuthentication(String token) {
        UserDetails userDetails = this.userDetailsService.loadUserByUsername(getUsername(token));
        return new UsernamePasswordAuthenticationToken(userDetails, "", userDetails.getAuthorities());
    }

    public String getUsername(String token) {
        return Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token).getBody().getSubject();
    }

    public Date getExpiration(String token) {
        return Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token).getBody().getExpiration();
    }

    // Достает из хедера токен с заголовком authorizationHeader.
    public String resolveToken(HttpServletRequest request) {
        String cookieValue;
        try {
            cookieValue = Objects.requireNonNull(Utility.getCookie(request, authorizationHeader)).getValue();
        } catch (NullPointerException ignored) {
            cookieValue = null;
        }
        return cookieValue;
    }

    public void removeCookieToken(HttpServletResponse response){
        Cookie cookie = new Cookie(authorizationHeader, "");
        cookie.setMaxAge(0);
        response.addCookie(cookie);
    }

    public void setCookieToken(HttpServletResponse response, User user){
        String token = createToken(user.getEmail(), user.getRole().name());
        response.addCookie(createCookie(token));
    }
}
