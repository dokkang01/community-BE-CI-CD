package kakaotechbootcamp.communityservice.dto;

import lombok.Getter;

import java.time.LocalDateTime;


public record PostSummaryResponse(Long id, String title, LocalDateTime createdTime, String authorNickname,
                                  String authorProfilePicture) {
    /* FIXME: 좋아요 댓글 조회수 기능 추가 후 다음 추가
    private final int likes;
    private final int comments;
    private final int views;
*/
}
