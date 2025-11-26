package kakaotechbootcamp.communityservice.service;

import kakaotechbootcamp.communityservice.entity.Post;
import kakaotechbootcamp.communityservice.entity.User;
import kakaotechbootcamp.communityservice.repository.PostRepository;
import kakaotechbootcamp.communityservice.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PostService {
    private final PostRepository postRepository;
    private final UserRepository userRepository;

    // 새로운 게시물 작성
    @Transactional
    public Post create(Long authorId, String title, String content) {
        User author = userRepository.findById(authorId).orElseThrow(() -> new IllegalArgumentException("user not found"));
        Post post = new Post(author, title, content);

        return postRepository.save(post);
    }

    public Post findById(Long id) {
        return postRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("post not found"));
    }
//    게시글 수정
    @Transactional
    public Post update(Long id, String title, String content) {
        Post post = findById(id);
        if (title != null) {
            post.setTitle(title);
        }
        if (content != null) {
            post.setContent(content);
        }
        post.setEdited(true);
        return post;
    }
//    게시글 삭제
    @Transactional
    public void delete(Long id) {
        postRepository.deleteById(id);
    }
}
