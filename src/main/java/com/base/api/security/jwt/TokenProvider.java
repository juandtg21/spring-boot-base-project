package com.base.api.security.jwt;

import com.base.api.config.AppProperties;
import com.base.api.dto.LocalUser;
import com.base.api.model.Role;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.security.Keys;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
public class TokenProvider {

    private static final Logger logger = LoggerFactory.getLogger(TokenProvider.class);
    private final AppProperties appProperties;

    public TokenProvider(AppProperties appProperties) {
        this.appProperties = appProperties;
    }

    public String createToken(Authentication authentication) {
        LocalUser userPrincipal = (LocalUser) authentication.getPrincipal();

        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + appProperties.getAuth().getTokenExpirationMsec());

        return Jwts.builder()
            .claim("sub", Long.toString(userPrincipal.getUser().getId()))
            .claim("iat", now.getTime())
            .claim("exp", expiryDate.getTime())
            .claim("email", userPrincipal.getUser().getEmail())
            .claim("displayName", userPrincipal.getUser().getDisplayName())
            .claim("provider", userPrincipal.getUser().getProvider())
            .claim("roles", userPrincipal.getUser().getRoles().stream().map(Role::getName).toArray(String[]::new))
            .claim("picture", userPrincipal.getUser().getPicture())
            .signWith(Keys.hmacShaKeyFor(appProperties.getAuth().getTokenSecret().getBytes()), Jwts.SIG.HS512)
            .compact();
    }


    public Claims getClaimsFromToken(String token) {
        return Jwts.parser()
            .verifyWith(Keys.hmacShaKeyFor(appProperties.getAuth().getTokenSecret().getBytes()))
            .build()
            .parseSignedClaims(token)
            .getPayload();
    }

    public boolean validateToken(String authToken) {
        try {
            Jwts.parser()
                .verifyWith(Keys.hmacShaKeyFor(appProperties.getAuth().getTokenSecret().getBytes()))
                .build()
                .parseSignedClaims(authToken);

            return true;
        } catch (Exception ex) {
            logger.error("Invalid JWT token: {}", ex.getMessage());
            return false;
        }
    }
}
