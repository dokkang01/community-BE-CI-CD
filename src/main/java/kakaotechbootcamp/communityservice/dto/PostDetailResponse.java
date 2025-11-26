package kakaotechbootcamp.communityservice.dto;

import java.time.LocalDateTime;
import java.util.List;

// TODO: 좋아요 댓글 조회수 기능 추가

public record PostDetailResponse(
        Long id,
        String title,
        LocalDateTime createdTime,
        String authorNickname,
        String authorProfilePicture,
        List<PostImageDTO> images,
        List<CommentItemResponse> comments,
        Long nextCursor,
        boolean hasNext
) {}