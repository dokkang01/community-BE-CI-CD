package kakaotechbootcamp.communityservice.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.security.PrivateKey;

@Entity
@Getter @Setter
@Table(name = "post_info")
public class PostInfo {
    @Id
    @Column(name = "post_id")
    private Long id;

    @OneToOne (fetch = FetchType.LAZY)
    @MapsId
    @JoinColumn(name = "post_id", foreignKey = @ForeignKey(name = "fk_postinfo_post"))
    private Post post;

    @Column (name = "view_count", nullable = false)
    private int viewCount = 0;

    @Column (name = "likes_count", nullable = false)
    private int likesCount = 0;

    @Column (name = "comments_count", nullable = false)
    private int commentsCount = 0;


    public static PostInfo of(Post post) {
        PostInfo info = new PostInfo();
        info.post = post;
        return info;
    }

    public void addViews(int delta)            {this.viewCount += delta;}
    public void incLikes()                      {this.likesCount++;}
    public void decLikes()                      {this.likesCount--;}
    public void incComments()                   {this.commentsCount++;}
    public void decComments()                   {this.commentsCount--;}


}
