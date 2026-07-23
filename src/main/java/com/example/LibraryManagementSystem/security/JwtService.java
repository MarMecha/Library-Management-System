package com.example.LibraryManagementSystem.security;

import org.springframework.stereotype.Service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;

import java.util.Date;

import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;

@Service
public class JwtService {
    
    private final String jwtSecret;
    private final long jwtExpiration;

    public JwtService(@Value("${jwt.secret}") String jwtSecret,
                    @Value("${jwt.expiration}") long jwtExpiration){
        
        this.jwtSecret = jwtSecret;
        this.jwtExpiration = jwtExpiration;
    }

    private SecretKey getSigningKey(){
        byte[] keyBytes = Decoders.BASE64.decode(jwtSecret);

        return Keys.hmacShaKeyFor(keyBytes);
    }

    public String generateToken(UserDetails userDetails){

        long currentTime = System.currentTimeMillis();

        // the 3 parts of JWT : header.payload.signature
        // PayLoad: subject , issuedAt , Expiration 
        return Jwts.builder()
            .subject(userDetails.getUsername()) //το username είναι το email.
            .issuedAt(new Date(currentTime))
            .expiration(new Date(currentTime + jwtExpiration))
            .signWith(getSigningKey()) //Υπογράφει το token. Αν κάποιος αλλάξει το payload, η υπογραφή δεν θα ταιριάζει.
            .compact(); //Μετατρέπει το JWT στην τελική μορφή
    }

    private Claims extractAllClaims(String token){
        return Jwts.parser()
            .verifyWith(getSigningKey())
            .build()
            .parseSignedClaims(token)
            .getPayload();
    }
    
    public String extractUsername(String token){
        return extractAllClaims(token).getSubject();
    }

    public boolean isTokenValid(String token, UserDetails userDetails){
        try {
            String username = extractUsername(token);
            
            return username.equals(userDetails.getUsername());
        } catch (JwtException | IllegalArgumentException exception) {
            return false;
        }
    }
}
