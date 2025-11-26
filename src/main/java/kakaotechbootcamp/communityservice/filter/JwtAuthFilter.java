package kakaotechbootcamp.communityservice.filter;

import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import kakaotechbootcamp.communityservice.jwt.JwtProvider;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Arrays;
import java.util.Optional;

/**
 * NOTE:
 * 모든 요청에서 JWT(Access Token)를 자동으로 추출하고 검증해서 인증괸 사용자라면 userId 정볼르 request에 심어주는 필터
 *  로그인 여부를 체크해주는 역할
 */
@Component
@RequiredArgsConstructor
public class JwtAuthFilter extends OncePerRequestFilter {
    private final JwtProvider jwtProvider;

    private static final String[] EXCLUDED_PATHS = {
            "/users/login", "/users/signup", "/users/check-email", "/users/check-nickname"
    };

    // NOTE: 특정 URL은 토큰 검사 생략
    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            return true; // preflight 통과 시키기
        }
        String path = request.getRequestURI();
        return Arrays.stream(EXCLUDED_PATHS).anyMatch(path::startsWith);
    }

    // 토큰 검사 -> 토큰 유효 -> 안증정보 저장후 통과
    // 토큰 검사 -> 토큰 무효 -> 401 응답
    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain chain
    ) throws IOException, ServletException {
        Optional<String> token = extractToken(request);

        if (token.isEmpty()) {
            chain.doFilter(request, response);
            return;
        }

        if (!validateAndSetAttributes(token.get(), request)) {
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }

        chain.doFilter(request, response);
    }


    // 토큰 추출
    private Optional<String> extractToken(HttpServletRequest request) {
        return extractTokenFromHeader(request)
                .or(() -> extractTokenFromCookie(request));
    }
    // 헤더에서 토큰 추출
    private Optional<String> extractTokenFromHeader(HttpServletRequest request) {
        return Optional.ofNullable(request.getHeader("Authorization"))
                .filter(header -> header.startsWith("Bearer "))
                .map(header -> header.substring(7));
    }
    // 쿠키에서 토큰 추출
    private Optional<String> extractTokenFromCookie(HttpServletRequest request) {
        return Optional.ofNullable(request.getCookies())
                .stream()
                .flatMap(Arrays::stream)
                .filter(cookie -> "accessToken".equals(cookie.getName()))
                .map(Cookie::getValue)
                .findFirst();
    }

    // 토큰 검증 및 요청 속성 설정
    private boolean validateAndSetAttributes(String token, HttpServletRequest request) {
        try {
            var jws = jwtProvider.parse(token);
            Claims body = jws.getBody();
            request.setAttribute("userId", Long.valueOf(body.getSubject()));
            return true;
        } catch (Exception exception) {
            return false;
        }
    }
}
