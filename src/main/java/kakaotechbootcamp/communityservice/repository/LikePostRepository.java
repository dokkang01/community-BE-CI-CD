package kakaotechbootcamp.communityservice.repository;

import kakaotechbootcamp.communityservice.entity.LikePost;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LikePostRepository extends JpaRepository<LikePost, Long> {
    boolean existsByPost_IdAndUser_Id(Long postId, Long userId);
    long countByPost_Id(Long postId);
    void deleteByPost_IdAndUser_Id(Long postId, Long userId);
}
