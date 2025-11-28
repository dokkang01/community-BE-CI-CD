package kakaotechbootcamp.communityservice.controller;

import kakaotechbootcamp.communityservice.entity.Comment;
import kakaotechbootcamp.communityservice.repository.CommentRepository;
import kakaotechbootcamp.communityservice.service.CommentService;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/posts/{postId}/comments")
@RequiredArgsConstructor
public class CommentController {
    private final CommentService commentService;
    private final CommentRepository commentRepository;

    //    댓글 생성
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CommentResponse create (@PathVariable Long postId, @RequestBody CreateCommentRequest request){
        Comment comment = commentService.create(postId, request.getAuthorId(), request.content);
        return CommentResponse.of(comment);
    }

    @GetMapping("/{commentId}")
    public CommentResponse get(@PathVariable Long postId, @PathVariable Long commentId){
        return CommentResponse.of(commentService.findByPostAndId(postId,commentId));
    }
// 댓글 수정
    @PatchMapping("/{commentId}")
    public CommentResponse update (@PathVariable Long postId, @PathVariable Long commentId, @RequestBody UpdateCommentRequest request){
        return CommentResponse.of(commentService.update(postId, commentId, request.content));
    }

    @DeleteMapping("/{commentId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long commentId) {
        commentService.delete(commentId);
    }

    @Data
    public static class CommentResponse {
        private final Long id;
        private final Long authorId;
        private final String content;

        public static CommentResponse of(Comment comment) {
            return new CommentResponse(comment.getId(),comment.getAuthor().getId(), comment.getContent());
        }

        public CommentResponse(Long id, Long authorId, String content) {
            this.id = id;
            this.authorId = authorId;
            this.content = content;
        }
    }

    @Data
    public static class CreateCommentRequest {
        private Long authorId;
        private String content;
    }

    @Data
    public static class UpdateCommentRequest {
        private String content;
    }


}
