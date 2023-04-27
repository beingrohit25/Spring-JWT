package com.security.jwt.jwtsecurity.Configuration;


import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import org.springframework.security.core.userdetails.UserDetails;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;

public class JwtService {

    private static final String SECRET_KEY = "645267556B58703273357638792F423F4528482B4D6251655368566D59713374";

    public String extractUsername(String jwt) {
        return null;
    }

    public String username(String token){
        return extractClaim(token, claims-> claims.getSubject());
    }

    public String generateToken(Map<String, Object> extraClaims, UserDetails userDetails){
        return Jwts
        .builder()
        .setClaims(extraClaims)
        .setSubject(userDetails.getUsername())
        .setIssuedAt(new Date(System.currentTimeMillis()))
        .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 *24))
        .signWith(getSigningKey(), SignatureAlgorithm.HS256)
        .compact();
    }

    public String generateToken(UserDetails userDetails){
        return generateToken(new HashMap<>(), userDetails);
    }
    
    public Boolean isTokenValid(String token, UserDetails userDetails){
        String username = extractUsername(token);
        return (username.equals(userDetails.getUsername())) && !isTokenExpired(token);
    }
    
    private boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    private Date extractExpiration(String token) {
        return extractClaim(token, claims-> claims.getExpiration());
    }

    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver){
        Claims allClaims = allClaims(token);
        return claimsResolver.apply(allClaims);
    }

    private Claims allClaims(String token){
        return Jwts
        .parserBuilder()
        .setSigningKey(getSigningKey())
        .build()
        .parseClaimsJws(token)
        .getBody();
    }

    //Gwetting the signing key
    private Key getSigningKey() {
        byte[] keyBytes = Decoders.BASE64.decode(SECRET_KEY);
        return Keys.hmacShaKeyFor(keyBytes);
    }

}
