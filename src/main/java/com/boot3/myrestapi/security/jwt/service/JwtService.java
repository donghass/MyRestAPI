package com.boot3.myrestapi.security.jwt.service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SecureDigestAlgorithm;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.security.Key;
import java.time.Instant;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.function.Function;

@Component
public class JwtService {
    public static final String SECRET = "5367566B59703373367639792F423F4528482B4D6251655468576D5A71347437";
    public static final SecretKey KEY = Keys.hmacShaKeyFor(SECRET.getBytes());
    private final static SecureDigestAlgorithm<SecretKey, SecretKey> ALGORITHM = Jwts.SIG.HS256;
    public static final int ACCESS_EXPIRE = 3600;

    // JWT 토큰에 저장된 정보를 추출
    private Claims extractAllClaims(String token) {
        return Jwts
                .parser()
                .verifyWith(KEY)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    // payload 의 "sub":"user@aa.com", subject 에 매핑된 값 주소 반환
    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    public Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    private Boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    public Boolean validateToken(String token, UserDetails userDetails) {
        // 토큰에 포함된 username(이메일) 추출
        final String username = extractUsername(token);
//        token 과 Entity(테이블)에 있는 username 가 같으면 true
        return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
    }
    // 토큰 검증
    public boolean validateToken(String token) {
        try {
            Jwts.parser()
                    .verifyWith(KEY)
                    .build()
                    .parseSignedClaims(token);
            return true;
        } catch (Exception ex) {
            throw new AuthenticationCredentialsNotFoundException(
                    "JWT was exprired or incorrect",
                    ex.fillInStackTrace());
        }
    }
    // JWT 토큰 생성
    public String generateToken(String userName){
        // ACCESS_EXPIRE 3600초 => 60분
        Date exprireDate = Date.from(Instant.now().plusSeconds(ACCESS_EXPIRE));

        return Jwts.builder()               // JWT 빌더 인스턴스 생성
                .signWith(KEY, ALGORITHM)   // 시크릿키와 암호화 알고리즘
                .subject(userName)          // payload 내부의 subject (유저)
                .issuedAt(new Date())       //   내부의 발급시간
                .expiration(exprireDate)    // 토큰 유효시간
                .compact();                 // 위 내용으로 만든 토큰값
    }

}