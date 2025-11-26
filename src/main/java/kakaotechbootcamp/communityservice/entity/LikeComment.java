package kakaotechbootcamp.communityservice.entity;
import jakarta.persistence.*;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table (name = "comment_like", uniqueConstraints = @UniqueConstraint(name="uq_comment_like", columnNames = {"comment_id","user_id"}),
        indexes = {
                @Index(name="idx_comment_like_user_id", columnList="user_id")
        })
@Getter
public class LikeComment {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "like_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "comment_id", nullable = false,
            foreignKey = @ForeignKey(name = "fk_comment_like_comment"))
    private Comment comment;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false, columnDefinition = "INT UNSIGNED",
            foreignKey = @ForeignKey(name = "fk_comment_like_user"))
    private User user;

    protected LikeComment() {}

    public LikeComment(Long id, Comment comment, User user) {
        this.id = id;
        this.comment = comment;
        this.user = user;
    }


    public static LikeComment of(Comment comment, User user) {
        LikeComment cl = new LikeComment();
        cl.comment = comment; cl.user = user;
        return cl;
    }
}