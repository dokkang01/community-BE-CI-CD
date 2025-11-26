package kakaotechbootcamp.communityservice.service;

import jakarta.servlet.http.HttpServletResponse;
import kakaotechbootcamp.communityservice.dto.LoginRequest;
import kakaotechbootcamp.communityservice.entity.RefreshToken;
import kakaotechbootcamp.communityservice.exception.BadRequestException;
import kakaotechbootcamp.communityservice.exception.UnprocessableEntityException;
import kakaotechbootcamp.communityservice.jwt.JwtProvider;
import kakaotechbootcamp.communityservice.repository.RefreshTokenRepository;
import org.springframework.http.ResponseCookie;
import org.springframework.transaction.annotation.Transactional;
import kakaotechbootcamp.communityservice.entity.User;
import kakaotechbootcamp.communityservice.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserService {
    private final UserRepository userRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final JwtProvider jwtProvider;

    private static final int ACCESS_TOKEN_VALIDITY_SECONDS = 15 * 60;
    private static final int REFRESH_TOKEN_VALIDITY_SECONDS = 14 * 24 * 3600;
//    사용자 회원가입
    @Transactional
    public User signUp(String email,  String password, String passwordCheck, String nickname,String profilePicture) {
        User user = new User(email, password, passwordCheck, nickname, profilePicture);
        if (!password.equals(passwordCheck)) {
//            비밀번호 확인이 일치해야함
            throw new UnprocessableEntityException("비밀번호 확인과 다릅니다");

        }
        return userRepository.save(user);
    }
    @Transactional
    public String login (LoginRequest loginRequest, HttpServletResponse response) {
        User user = userRepository.findByEmail(loginRequest.getEmail()).orElseThrow(() -> new BadRequestException("이메일을 확인해주세요"));

        if(!user.getPassword().equals(loginRequest.getPassword())){
            throw new BadRequestException("비밀번호를 확인해주세요");
        }
        var tokenResponse = generateAndSaveTokens(user);
        addTokenCookies(response, tokenResponse);
        return tokenResponse.accessToken();
    }

    @Transactional
    public void logout(HttpServletResponse response) {
        addTokenCookie(response, "accessToken", null, 0);
        addTokenCookie(response, "refreshToken", null, 0);
    }

    @Transactional
    public TokenResponse refreshTokens(String refreshToken, HttpServletResponse response) {
        var parsedRefreshToken = jwtProvider.parse(refreshToken);

        RefreshToken entity = refreshTokenRepository.findByTokenAndRevokedFalse(refreshToken).orElse(null);
        if( entity == null || entity.getExpiresAt().isBefore(Instant.now())){
            return null;
        }
        // AT 새로 발급 되면 RT도 재발급 받으면서 보안성을 높힌다
        String newAccessToken = jwtProvider.createAccessToken(entity.getUserId());
        String newRefreshToken = jwtProvider.createRefreshToken(entity.getUserId());

        entity.setToken(newRefreshToken);
        entity.setExpiresAt(Instant.now().plusSeconds(REFRESH_TOKEN_VALIDITY_SECONDS));
        entity.setRevoked(false);
        refreshTokenRepository.save(entity);


        addTokenCookie(response, "accessToken", newAccessToken, ACCESS_TOKEN_VALIDITY_SECONDS);
        addTokenCookie(response, "refreshToken", newRefreshToken, REFRESH_TOKEN_VALIDITY_SECONDS);
        return new TokenResponse(newAccessToken, newRefreshToken);
    }

    public User findById(Long id) {
        return userRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("User not found"));
    }

    public User getReferenceById(Long id) {
        return userRepository.getReferenceById(id);
    }
//    사용자 닉네입, 프로필 사진 변경
    @Transactional
    public User update(Long id, String nickname, String profilePicture) {
        User user = findById(id);
        if (nickname != null) {
            user.setNickname(nickname);
        }
        if (profilePicture != null) {
            user.setProfilePicture(profilePicture);
        }

        return user;
    }

    @Transactional
    public void delete(Long id) {
        userRepository.deleteById(id);
    }

    private void addTokenCookies(HttpServletResponse response, TokenResponse tokenResponse) {
        addTokenCookie(response, "accessToken", tokenResponse.accessToken(), ACCESS_TOKEN_VALIDITY_SECONDS);
        addTokenCookie(response, "refreshToken", tokenResponse.refreshToken(), REFRESH_TOKEN_VALIDITY_SECONDS);
    }

    private void addTokenCookie(HttpServletResponse response, String name, String value, int maxAge) {
        ResponseCookie cookie = ResponseCookie.from(name, value == null ? "" : value)
                .httpOnly(true)
                .secure(false)
                .path("/")
                .sameSite("Lax")
                .maxAge(maxAge)
                .build();
        response.addHeader("Set-Cookie", cookie.toString());
    }
    /** Access / Refresh 토큰을 새로 발급하고 DB에 저장 */
    private TokenResponse generateAndSaveTokens(User user) {
        String accessToken = jwtProvider.createAccessToken(user.getId());
        String refreshToken = jwtProvider.createRefreshToken(user.getId());
        RefreshToken refreshEntity = refreshTokenRepository.findByUserId(user.getId()).orElseGet(RefreshToken::new);

        refreshEntity.setUserId(user.getId());
        refreshEntity.setToken(refreshToken);
        refreshEntity.setExpiresAt(Instant.now().plusSeconds(REFRESH_TOKEN_VALIDITY_SECONDS));
        refreshEntity.setRevoked(false);

        refreshTokenRepository.save(refreshEntity);
        return new TokenResponse(accessToken, refreshToken);
    }
    public record TokenResponse(String accessToken, String refreshToken) { }
}
