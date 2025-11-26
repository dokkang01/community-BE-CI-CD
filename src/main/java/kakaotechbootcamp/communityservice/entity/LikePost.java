package kakaotechbootcamp.communityservice.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "post_like",
        uniqueConstraints = @UniqueConstraint(name="uq_post_like", columnNames = {"post_id","user_id"}),
        indexes = {
                @Index(name="idx_post_like_user_id", columnList="user_id")
        })
@Getter
public class LikePost {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "like_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id", nullable = false, foreignKey = @ForeignKey(name = "fk_post_like_post"))
    private Post post;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false, columnDefinition = "INT UNSIGNED", foreignKey = @ForeignKey(name = "fk_post_like_user"))
    private User user;

    protected LikePost() {}

    public LikePost(Post post, User user) {
        this.post = post;
        this.user = user;
    }

    public static LikePost of(Post post, User user) {
        LikePost lp = new LikePost();
        lp.post = post; lp.user = user;
        return lp;
    }
}
