package kakaotechbootcamp.communityservice.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter @Setter
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id", columnDefinition = "INT UNSIGNED")
    private Long id;                                                // 사용자 아이디

    @Column(name = "nickname", length = 10, nullable = false)
    private String nickname;                                       // 사용자 닉네임

    @Column(name = "email", length = 320, nullable = false)
    private String email;                                         // 사용자 이메일

    @Column(name = "password", nullable = false)
    private String password;                                      // 사용자 비밀번호

    @Transient
    private String checkPassword;
    @Column(name = "profile_picture", nullable = true)
    private String profilePicture;                                // 사용자 프로필 사진 URL

    @OneToMany (mappedBy = "author", fetch = FetchType.LAZY)
    private List<Post> posts = new ArrayList<>();


    protected User() {}

    public User(String email,String password, String nickname, String profilePicture) {
        this.email = email;
        this.password = password;
        this.nickname = nickname;
        this.profilePicture = profilePicture;
    }

    public User(String email,String password, String checkPassword, String nickname, String profilePicture) {
        this.email = email;
        this.password = password;
        this.checkPassword = checkPassword;
        this.nickname = nickname;
        this.profilePicture = profilePicture;
    }

    // 편의 메서드 (양방향 연관관계 일관성 유지)
    public void addPost(Post post) {
        this.posts.add(post);
        post.setAuthor(this);
    }

    public void removePost(Post post) {
        this.posts.remove(post);
        post.setAuthor(null);
    }


}
