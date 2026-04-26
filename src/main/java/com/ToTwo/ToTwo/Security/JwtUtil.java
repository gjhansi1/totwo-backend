 package com.ToTwo.ToTwo.Security; 

 

import io.jsonwebtoken.Claims; 

import io.jsonwebtoken.Jwts; 

import io.jsonwebtoken.SignatureAlgorithm; 

import io.jsonwebtoken.security.Keys; 

import org.springframework.beans.factory.annotation.Value; 

import org.springframework.stereotype.Component; 

 

import javax.crypto.SecretKey; 

import java.util.Date; 

 

@Component 

public class JwtUtil { 

 

private final SecretKey secretKey; 

private final long validity = 5 * 60 * 60 * 1000; // 5 hours 

 

// Load key from application.properties 

public JwtUtil(@Value("${jwt.secret}") String secret) { 

this.secretKey = Keys.hmacShaKeyFor(secret.getBytes()); 

} 

 

public String generateToken(String email) { 

return Jwts.builder() 

.setSubject(email) 

.setIssuedAt(new Date()) 

.setExpiration(new Date(System.currentTimeMillis() + validity)) 

.signWith(secretKey, SignatureAlgorithm.HS256) 

.compact(); 

} 

 

public String extractEmail(String token) { 

Claims claims = Jwts.parserBuilder() 

.setSigningKey(secretKey) 

.build() 

.parseClaimsJws(token) 

.getBody(); 

return claims.getSubject(); 

} 

 

public boolean validateToken(String token, String email) { 

try { 

Claims claims = Jwts.parserBuilder() 

.setSigningKey(secretKey) 

.build() 

.parseClaimsJws(token) 

.getBody(); 

return claims.getSubject().equals(email) && !claims.getExpiration().before(new Date()); 

} catch (Exception e) { 

return false; 

} 

} 

} 