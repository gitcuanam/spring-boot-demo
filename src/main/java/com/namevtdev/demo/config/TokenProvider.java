package com.namevtdev.demo.config;

import io.jsonwebtoken.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.core.io.ClassPathResource;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Component;

import java.security.KeyPair;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Date;

import static com.namevtdev.demo.config.Constants.*;

@Component
@Slf4j
@EnableConfigurationProperties(AuthenticationProperties.class)
public class TokenProvider implements InitializingBean {

    private final AuthenticationProperties properties;
    private long accessTokenExpiresIn;
    private long refreshTokenExpiresIn;
    //Keypair: publicKey(server) and privateKey(client)
    private KeyPair keyPair;

    public TokenProvider(AuthenticationProperties properties) {
        this.properties = properties;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        this.keyPair = keyPair(properties.getKeyStore(), properties.getKeyStorePassword(), properties.getKeyAlias());
        this.accessTokenExpiresIn = properties.getAccessTokenExpiresIn().toMillis();
        this.refreshTokenExpiresIn = properties.getRefreshTokenExpiresIn().toMillis();
    }

    private KeyPair keyPair(String keyStore, String password, String alias) {
        KeyStoreKeyFactory keyStoreKeyFactory =
                new KeyStoreKeyFactory(
                        new ClassPathResource(keyStore),
                        password.toCharArray());
        return keyStoreKeyFactory.getKeyPair(alias);
    }

    public String createClientToken(String clientId) {
        long now = Instant.now().toEpochMilli();
        Date validity = new Date(now + this.accessTokenExpiresIn);
        return Jwts.builder()
                .setSubject(clientId)
                .claim(AUTHORITY_TYPE, CLIENT)
                .signWith(keyPair.getPrivate(), SignatureAlgorithm.RS256)
                .setExpiration(validity)
                .compact();
    }

    public String createToken(Authentication authentication, Long userId) {
        long now = Instant.now().toEpochMilli();
        Date validity = new Date(now + this.accessTokenExpiresIn);

        return Jwts.builder()
                .setSubject(authentication.getName())
                .claim(USER_ID, userId)
                .signWith(keyPair.getPrivate(), SignatureAlgorithm.RS256)
                .setExpiration(validity)
                .compact();
    }

    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder().setSigningKey(keyPair.getPublic()).build().parseClaimsJws(token);
            return true;
        } catch (io.jsonwebtoken.security.SignatureException | MalformedJwtException e) {
            log.info("Invalid JWT signature.");
        } catch (ExpiredJwtException e) {
            log.info("Expired JWT token.");
        } catch (UnsupportedJwtException e) {
            log.info("Unsupported JWT token.");
        } catch (IllegalArgumentException e) {
            log.info("JWT token compact of handler are invalid.");
        }
        return false;
    }

    //Bên phía client giữ privateKey
    public String createRefreshToken(Long userId) {
        long now = Instant.now().toEpochMilli();
        Date validity = new Date(now + this.refreshTokenExpiresIn);

        return Jwts.builder()
                .setSubject(userId.toString())
                .claim(AUTHORITY_TYPE, REFRESH_TOKEN)
                .signWith(keyPair.getPrivate(), SignatureAlgorithm.RS256)
                .setExpiration(validity)
                .compact();
    }

    //Bên phía server giữ publicKey
    public boolean validateRefreshToken(String token) {
        try {
            Jwts.parserBuilder().setSigningKey(keyPair.getPublic()).build().parseClaimsJws(token);
            return true;
        } catch (io.jsonwebtoken.security.SignatureException | MalformedJwtException e) {
            log.info("Invalid JWT signature.");
        } catch (ExpiredJwtException e) {
            log.info("Expired JWT token.");
        } catch (UnsupportedJwtException e) {
            log.info("Unsupported JWT token.");
        } catch (IllegalArgumentException e) {
            log.info("JWT token compact of handler are invalid.");
        }
        return false;
    }

    public Authentication getAuthentication(String token) {
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(keyPair.getPublic()).build()
                .parseClaimsJws(token)
                .getBody();
//        Claims claims = Jwts.parser()
//                .setSigningKey(keyPair.getPublic())
//                .parseClaimsJws(token)
//                .getBody();
        User principal = new User(claims.getSubject(), "", new ArrayList<>());
        return new UsernamePasswordAuthenticationToken(principal, token, new ArrayList<>());
    }
}
