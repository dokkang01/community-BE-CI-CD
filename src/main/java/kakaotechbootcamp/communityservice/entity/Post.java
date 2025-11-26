package kakaotechbootcamp.communityservice.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter @Setter
public class Post {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "post_id", nullable = false)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false, foreignKey =  @ForeignKey(name = "fk_post_user"))
    private User author;

    @Column(name = "title", length = 26, nullable = false)
    private String title;

    @Column(name = "content", nullable = false, columnDefinition = "MEDIUMTEXT")
    private String content;

    @Column(name = "created_time", nullable = false, updatable = false)
    private java.time.LocalDateTime createdTime;

    @Column(name = "is_edited", nullable = false)
    private boolean isEdited = false;                                               // 기본적으로 거짓으로 설정


    protected Post() {}

    public Post(User author, String title, String content) {
        this.author = author;
        this.title = title;
        this.content = content;
        this.createdTime = java.time.LocalDateTime.now();
        this.isEdited = false;
    }

    // 관계 편의 메서드
    void setAuthor(User author) {
        this.author = author;
    }
}
