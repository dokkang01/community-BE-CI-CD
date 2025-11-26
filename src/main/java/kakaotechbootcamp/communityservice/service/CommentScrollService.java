package kakaotechbootcamp.communityservice.service;

import com.querydsl.jpa.impl.JPAQueryFactory;
import kakaotechbootcamp.communityservice.dto.CommentItemResponse;
import kakaotechbootcamp.communityservice.dto.CommentSliceResponse;
import kakaotechbootcamp.communityservice.entity.Comment;
import kakaotechbootcamp.communityservice.entity.QComment;
import kakaotechbootcamp.communityservice.entity.QUser;
import kakaotechbootcamp.communityservice.exception.BadRequestException;
import kakaotechbootcamp.communityservice.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CommentScrollService {
    private final JPAQueryFactory queryFactory;
    private final PostRepository postRepository;

    public CommentSliceResponse listByPostCursor(Long postId, Long cursor, int size) {
        if (!postRepository.existsById(postId)) {
            throw new BadRequestException("post not found");
        }

        QComment c = QComment.comment;
        QUser u = QUser.user;

        List<Comment> rows = queryFactory.selectFrom(c)
                .join(c.author, u).fetchJoin()
                .where(
                        c.post.id.eq(postId),
                        cursor != null ? c.id.gt(cursor) : null
                )
                .orderBy(c.createdTime.desc())
                .limit(size + 1) // hasNext 판별 위해 +1
                .fetch();

        boolean hasNext = rows.size() > size;
        if (hasNext) rows = rows.subList(0, size);

        Long nextCursor = rows.isEmpty() ? null : rows.get(rows.size() - 1).getId();

        var items = rows.stream()
                .map(cm -> new CommentItemResponse(
                        cm.getContent(),
                        cm.getCreatedTime(),
                        cm.isEdited(),
                        cm.getAuthor().getNickname(),
                        cm.getAuthor().getProfilePicture()
                ))
                .toList();

        return new CommentSliceResponse(items, nextCursor, hasNext);
    }
}
