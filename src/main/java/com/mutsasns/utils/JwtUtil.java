package com.mutsasns.utils;

import com.mutsasns.exception.ErrorCode;
import io.jsonwebtoken.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.neo4j.Neo4jProperties;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.List;

@Slf4j
public class JwtUtil {

    public static String getUserName(String token, String secretKey){
        return Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token)
                .getBody().get("userName", String.class);
    }


    public static String createToken(String userName, String key, long expiredTimeMs) {
        Claims claims = Jwts.claims();
        claims.put("userName", userName);

        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + expiredTimeMs))
                .signWith(SignatureAlgorithm.HS256, key)
                .compact();
    }
}
