package kakaotechbootcamp.communityservice.entity;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;

@SpringBootTest
@Transactional
public class PostTest {
    @PersistenceContext
    private EntityManager entityManager;
    @Test
    @Rollback(false)
    void oneToManyTest(){
        User user = new User("test@gmail.com", "1q2w3e4r!", "tester", "http://image.com/imagepath/image.jpg");
        entityManager.persist(user);

        Post post = new Post(user, "게시글", "게시글 내용입니다");
        Post post1 = new Post(user, "게시글2", "2번째 게시글입니다");
        Post post2 = new Post(user, "게시글3", "3번째 게시글입니다");

        user.addPost(post);
        user.addPost(post1);
        user.addPost(post2);

        user.addPost(post);
        user.addPost(post1);
        user.addPost(post2);
        entityManager.persist(post);
        entityManager.persist(post1);
        entityManager.persist(post2);
        entityManager.persist(post1);
        entityManager.persist(post2);

        entityManager.flush();
        entityManager.clear();
        // 유저 다시 조회 후 posts 컬렉션으로 연관 게시글 확인
        User findUser = entityManager.find(User.class, user.getId());
        System.out.println("조회된 유저 닉네임 = " + findUser.getNickname());
        System.out.println("연관된 게시글 수 = " + findUser.getPosts().size());
        findUser.getPosts().forEach(posts ->
                System.out.println("post.id = " + posts.getId() + ", title = " + posts.getTitle())
        );

        // 특정 게시글 다시 조회 후 author 로 유저 확인
        Post findPost = entityManager.find(Post.class, post.getId());
        System.out.println("조회된 게시글 제목 = " + findPost.getTitle());
        System.out.println("작성자 닉네임 = " + findPost.getAuthor().getNickname());
    }

}