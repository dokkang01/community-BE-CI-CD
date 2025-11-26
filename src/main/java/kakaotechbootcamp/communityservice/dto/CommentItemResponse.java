package kakaotechbootcamp.communityservice.dto;

import java.time.LocalDateTime;

public record CommentItemResponse (
        String content,
        LocalDateTime createdTime,
        boolean isEdited,
        String authorNickname,
        String authorProfilePicture
){}
