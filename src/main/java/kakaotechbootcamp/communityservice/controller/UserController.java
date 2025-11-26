package kakaotechbootcamp.communityservice.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import kakaotechbootcamp.communityservice.dto.*;
import kakaotechbootcamp.communityservice.entity.User;
import kakaotechbootcamp.communityservice.repository.UserRepository;
import kakaotechbootcamp.communityservice.service.UserService;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.HttpStatus;

import org.springframework.web.server.ResponseStatusException;

import java.util.Map;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;
    private final UserRepository userRepository;

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("/signup")
    public UserResponse create(@RequestBody CreateUserRequest request) {
        User saved = userService.signUp(request.getEmail(), request.getPassword(), request.getPasswordCheck(), request.getNickname(), request.getProfilePicture());
        return UserResponse.of(saved);
    }

    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody LoginRequest loginRequest, HttpServletResponse response) {
        String loginResult = userService.login(loginRequest, response);
        return ResponseEntity.ok(loginResult);
    }
    @PostMapping("/logout")
    public ResponseEntity<String> logout(HttpServletResponse response) {
        userService.logout(response);
        return ResponseEntity.ok("로그아웃 성공");
    }

    @PostMapping("/refresh")
    @ResponseBody
    public Map<String, String> refresh(@CookieValue(value = "refreshToken", required = false) String refreshToken, HttpServletResponse response) {
        if (refreshToken == null) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return Map.of("error", "Refresh token missing");
        }
        try {
            var tokenRes = userService.refreshTokens(refreshToken, response);
            if (tokenRes == null) {
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                return Map.of("error", "Refresh token invalid or expired");
            }
            return Map.of(
                    "accessToken", tokenRes.accessToken(),
                    "refreshToken", tokenRes.refreshToken()
            );
        } catch (ResponseStatusException exception) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return Map.of("error", "Refresh token invalid or expired");

        }
    }
    @GetMapping("/me")
    public ResponseEntity<User> userDetail(HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");

        if (userId == null) {
            return ResponseEntity.status(401).build();
        }

        User user = userService.findById(userId);
        if (user == null) {
            return ResponseEntity.status(401).build();
        }

        return ResponseEntity.ok(user);
    }

    @GetMapping(("/{id}"))
    public UserResponse findById(@PathVariable Long id) {
        return UserResponse.of(userService.findById(id));
    }

    @PatchMapping("/{id}")
    public UserResponse update(@PathVariable Long id, @RequestBody UpdateUserRequest request) {
        User updatedUser = userService.update(id, request.getNickname(), request.getProfilePicture());
        return UserResponse.of(updatedUser);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        userService.delete(id);
    }

    /**
     * 이메일 중복검사
     */
    @GetMapping("/check-email")
    public boolean checkEmail(@RequestParam String email) {
        return userRepository.existsByEmail(email);
    }

    /**
     * 닉네임 중복검사
     */
    @GetMapping("/check-nickname")
    public boolean checkNickname(@RequestParam String nickname) {
        return userRepository.existsByNickname(nickname);
    }

    @Data
    public static class CreateUserRequest {
        private String email;
        private String password;
        private String passwordCheck;
        private String nickname;
        private String profilePicture;
    }

    @Data
    public static class UserResponse {
        private Long id;
        private String email;
        private String nickname;
        private String profilePicture;

        public static UserResponse of(User user) {
            return new UserResponse(
                    user.getId(),
                    user.getEmail(),
                    user.getNickname(),
                    user.getProfilePicture()
            );

        }

        public UserResponse(long id, String email, String nickname, String profilePicture) {
            this.id = id;
            this.email = email;
            this.nickname = nickname;
            this.profilePicture = profilePicture;
        }
    }

    @Data
    public static class UpdateUserRequest {
        private String nickname;
        private String profilePicture;
    }

}