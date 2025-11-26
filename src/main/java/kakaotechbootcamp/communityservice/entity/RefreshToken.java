package kakaotechbootcamp.communityservice.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.time.Instant;

@Entity
@Getter @Setter
@RequiredArgsConstructor
public class RefreshToken {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private long userId;

    @Column(unique = true, nullable = false)
    private String token;

    private Instant expiresAt;
    private boolean revoked;
}
