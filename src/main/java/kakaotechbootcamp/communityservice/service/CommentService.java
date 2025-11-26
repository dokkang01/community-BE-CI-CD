package kakaotechbootcamp.communityservice.service;

import kakaotechbootcamp.communityservice.entity.Comment;
import kakaotechbootcamp.communityservice.entity.Post;
import kakaotechbootcamp.communityservice.entity.User;
import kakaotechbootcamp.communityservice.exception.BadRequestException;
import kakaotechbootcamp.communityservice.repository.CommentRepository;
import kakaotechbootcamp.communityservice.repository.PostRepository;
import kakaotechbootcamp.communityservice.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CommentService {
    private final CommentRepository commentRepository;
    private final PostRepository postRepository;
    private final UserRepository userRepository;

    @Transactional
    public Comment create (Long postId, Long authorId, String content) {
        if (content == null) {
            throw new BadRequestException("댓글 내용을 입력하세요");
        }
        Post post = postRepository.findById(postId).orElseThrow(() -> new BadRequestException("없는 게시물"));
        User author = userRepository.findById(authorId).orElseThrow(() -> new BadRequestException("사용자 없음"));

        Comment comment = new Comment(post, author, content);
        return commentRepository.save(comment);
    }
    public Comment findByPostAndId(Long postId, Long commentId) {
        Comment c = commentRepository.findById(commentId)
                .orElseThrow(() -> new BadRequestException("게시글 없음"));
        if (!c.getPost().getId().equals(postId))
            throw new BadRequestException("게시글 없음");
        return c;
    }
    @Transactional
    public Comment update(Long postId, Long commentId,  String content) {
        if (content == null || content.isBlank())
            throw new BadRequestException("내용을 입력해주세요");

        Comment comment = findByPostAndId(postId, commentId);
        comment.setContent(content);
        comment.setEdited(true); // 수정 플래그
        return comment;
    }
    @Transactional
    public void delete(Long commentId) {
        if (!commentRepository.existsById(commentId))
            throw new BadRequestException("게시글을 찾을 수 없습니다");
        commentRepository.deleteById(commentId);
    }
}
