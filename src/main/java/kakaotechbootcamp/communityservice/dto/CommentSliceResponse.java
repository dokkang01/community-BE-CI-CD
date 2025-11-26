package kakaotechbootcamp.communityservice.dto;

import java.util.List;

public record CommentSliceResponse (
        List<CommentItemResponse> items,
        Long nextCursor,
        boolean hasNext
) {}
