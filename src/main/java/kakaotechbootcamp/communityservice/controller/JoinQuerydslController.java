package kakaotechbootcamp.communityservice.controller;

import kakaotechbootcamp.communityservice.dto.PostDetailResponse;
import kakaotechbootcamp.communityservice.dto.PostSummaryResponse;
import kakaotechbootcamp.communityservice.service.JoinQuerydslService;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
@RestController
@RequestMapping("/api/board")
public class JoinQuerydslController {
    private final JoinQuerydslService joinQuerydslService;

    public JoinQuerydslController(JoinQuerydslService joinQuerydslService) {
        this.joinQuerydslService = joinQuerydslService;
    }

    @GetMapping("/posts")
    public List<PostSummaryResponse> inner() {
        return joinQuerydslService.listRecentPosts();
    }

    @GetMapping("/posts/{postId}")
    public Map<String, Object> detail(@PathVariable Long postId,
                                      @RequestParam(required = false) Long cursor,
                                      @RequestParam(defaultValue = "20") int size) {
        PostDetailResponse detail = joinQuerydslService.getPostDetailWithComments(postId, cursor, size);
        Map<String, Object> body = new HashMap<>();
        body.put("message", "post_detail");
        body.put("data", detail);
        return body;
    }
}
