package kakaotechbootcamp.communityservice.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter @Setter
public class Comment {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false, foreignKey =  @ForeignKey(name = "fk_comments_user"))
    private User author;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id", nullable = false, foreignKey =  @ForeignKey(name = "fk_comments_post"))
    private Post post;

    @Column(name = "content", nullable = false, columnDefinition = "TEXT")
    private String content;

    @Column(name = "created_time", nullable = false, updatable = false)
    private java.time.LocalDateTime createdTime;

    @Column(name = "is_edited", nullable = false)
    private boolean isEdited = false;

    protected Comment() {}

    public Comment(Post post, User author, String content) {
        this.post = post;
        this.author = author;
        this.content = content;
        this.createdTime = java.time.LocalDateTime.now();
        this.isEdited = false;
    }

    void setAuthor(User author) {
        this.author = author;
    }

    void setPost(Post post) {
        this.post = post;
    }

    void editContent(String content) {
        this.content = content;
    }

}
