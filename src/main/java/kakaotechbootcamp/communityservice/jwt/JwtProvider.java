package kakaotechbootcamp.communityservice.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.time.Instant;
import java.util.Base64;
import java.util.Date;
import java.util.UUID;

/**
 *  NOTE: JWT 생성 및 검증을 하는 클래스
 *        AT, RT 생성 담당
 */
@Component
public class JwtProvider {
    // 서명 키 설정
    private final Key key = Keys.hmacShaKeyFor(
            Base64.getDecoder().decode("YWRhcHRlcnphZGFwdGVyemFkYXB0ZXJ6YWRhcHRlcnphZGFwdGVyeg==")
    );

    // access Token 생성
    public String createAccessToken(Long userId) {
        long accessTtlSec = 15 * 60;
        return Jwts.builder()
                .setSubject(String.valueOf(userId))
                .setIssuedAt(new Date())
                .setExpiration(Date.from(Instant.now().plusSeconds(accessTtlSec)))
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    public Jws<Claims> parse(String jwt) {
        return Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(jwt);
    }

    public String createRefreshToken(Long userId) {
        long refreshTtlSec = 14L * 24 * 3600; // 14일
        return Jwts.builder()
                .setSubject(String.valueOf(userId))
                .claim("typ", "refresh")
                .setId(UUID.randomUUID().toString())
                .setIssuedAt(new Date())
                .setExpiration(Date.from(Instant.now().plusSeconds(refreshTtlSec)))
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }
}