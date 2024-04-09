package io.mohajistudio.tangerine.prototype.domain.post.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@SuperBuilder
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "trending_post")
public class TrendingPost {
    @Id
    private Long id;

    @OneToOne
    private Post post;

    private double score;
}
