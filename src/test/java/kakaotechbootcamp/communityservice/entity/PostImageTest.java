package kakaotechbootcamp.communityservice.entity;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;

@SpringBootTest
@Transactional
class PostImageTest {
    @PersistenceContext
    private EntityManager entityManager;
    @Test
    @Rollback(false)
    void postImageTest(){
        User user = new User("test@gmail.com", "1q2w3e4r!", "tester", "http://image.com/imagepath/image.jpg");
        entityManager.persist(user);
        Post post = new Post(user, "게시글", "게시글 내용입니다");
        user.addPost(post);
        entityManager.persist(post);
        PostImage postImage1 = new PostImage("http://image.com/imagepath/post_image1.jpg", 1);
        PostImage postImage2 = new PostImage("http://image.com/imagepath/post_image2.jpg", 2);

        postImage1.setPost(post);
        postImage2.setPost(post);
        entityManager.persist(postImage1);
        entityManager.persist(postImage2);
        entityManager.flush();
        entityManager.clear();
    }
}