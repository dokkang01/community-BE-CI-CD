package kakaotechbootcamp.communityservice.service;

import com.querydsl.jpa.impl.JPAQueryFactory;
import kakaotechbootcamp.communityservice.dto.CommentItemResponse;
import kakaotechbootcamp.communityservice.dto.PostDetailResponse;
import kakaotechbootcamp.communityservice.dto.PostImageDTO;
import kakaotechbootcamp.communityservice.dto.PostSummaryResponse;
import kakaotechbootcamp.communityservice.entity.*;
import kakaotechbootcamp.communityservice.exception.BadRequestException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.List;

// NOTE: 게시판 불러오기 기능
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class JoinQuerydslService {
    private final JPAQueryFactory queryFactory;


    public List<PostSummaryResponse> listRecentPosts() {
        QPost p = QPost.post;
        QUser u = QUser.user;
        List<Post> posts = queryFactory.selectFrom(p)
                .join(p.author, u)
                .orderBy(p.createdTime.desc(), p.id.desc())
                .limit(30)
                .fetch();
        return posts.stream()
                .map(post -> new PostSummaryResponse(
                        post.getId(),
                        post.getTitle(),
                        post.getCreatedTime(),
                        post.getAuthor().getNickname(),
                        post.getAuthor().getProfilePicture()
                ))
                .toList();
    }

    //    NOTE: 게시글 상세보기
    public PostDetailResponse getPostDetailWithComments(Long postId, Long cursor, int size) {
        QPost p = QPost.post;
        QUser u  = QUser.user;

        // 1) 게시글 + 작성자 (fetchJoin으로 N+1 방지)
        Post post = queryFactory.selectFrom(p)
                .join(p.author, u).fetchJoin()
                .where(p.id.eq(postId))
                .fetchOne();

        if (post == null) throw new BadRequestException("post not found");

        // 2) 이미지 목록 (정렬: position ASC, id ASC)
        QPostImage pi = QPostImage.postImage;
        List<PostImage> images = queryFactory.selectFrom(pi)
                .where(pi.post.id.eq(postId))
                .orderBy(pi.position.asc(), pi.id.asc())
                .fetch();

        var imageDtos = images.stream()
                .map(img -> new PostImageDTO(img.getId(), img.getImageUrl(), img.getPosition()))
                .toList();

        // 3) 댓글 무한스크롤 (ASC, cursor > id)
        QComment c = QComment.comment;
        List<Comment> cmts = queryFactory.selectFrom(c)
                .join(c.author, u).fetchJoin()
                .where(
                        c.post.id.eq(postId),
                        cursor != null ? c.id.gt(cursor) : null
                )
                .orderBy(c.id.asc())
                .limit(size + 1) // hasNext 판별 위해 +1
                .fetch();

        boolean hasNext = cmts.size() > size;
        if (hasNext) cmts = cmts.subList(0, size);

        Long nextCursor = cmts.isEmpty() ? null : cmts.get(cmts.size() - 1).getId();

        var commentDtos = cmts.stream()
                .map(cm -> new CommentItemResponse(
                        cm.getContent(),
                        cm.getCreatedTime(),
                        cm.isEdited(),
                        cm.getAuthor().getNickname(),
                        cm.getAuthor().getProfilePicture()
                ))
                .toList();

        // 4) 통합 응답
        return new PostDetailResponse(
                post.getId(),
                post.getTitle(),
                post.getCreatedTime(),
                post.getAuthor().getNickname(),
                post.getAuthor().getProfilePicture(),
                imageDtos,
                commentDtos,
                nextCursor,
                hasNext
        );
    }
}
