package kakaotechbootcamp.communityservice.config;

import kakaotechbootcamp.communityservice.entity.Post;
import kakaotechbootcamp.communityservice.entity.User;
import kakaotechbootcamp.communityservice.repository.PostRepository;
import kakaotechbootcamp.communityservice.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.transaction.annotation.Transactional;

import java.util.stream.IntStream;

@Configuration
@Profile("dev")
@RequiredArgsConstructor
public class SeedConfig {
    private final UserRepository userRepository;
    private final PostRepository postRepository;

    @Bean
    ApplicationRunner seedRunner() {
        return args -> seed(); // 부트 기동 후 1회 실행
    }

    @Transactional
    void seed() {
        if (userRepository.count() >= 10 && postRepository.count() >= 10) return;

        IntStream.rangeClosed(1, 10).forEach(i -> {
            User user = new User("tester" + i + "@gmail.com", "1Q2w3e4r!" + i, "tester" + i, "http://image.com/test_image" + i + ".png");
            userRepository.save(user);

            Post post = new Post(user, "title"+i, "content"+i);
            postRepository.save(post);
        });
    }
}
