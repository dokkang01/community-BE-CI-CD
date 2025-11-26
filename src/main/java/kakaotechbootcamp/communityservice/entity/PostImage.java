package kakaotechbootcamp.communityservice.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter @Setter
public class PostImage {
    @Id @GeneratedValue
    @Column(name = "image_id", nullable = false)
    private long id;
    @Column(name = "image_url", length = 255, nullable = false)
    private String imageUrl;
    @Column(name = "position", nullable = false)
    private int position;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id", nullable = false, foreignKey =  @ForeignKey(name = "fk_post_image_post"))
    private Post post;

    protected PostImage() {}
    public PostImage(String imageUrl, int position) {
        this.imageUrl = imageUrl;
        this.position = position;
        this.post = post;
    }

    void setPost(Post post) {
        this.post = post;
    }
}
