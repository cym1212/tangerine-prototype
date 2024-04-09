package io.mohajistudio.tangerine.prototype.domain.post.domain;

import io.mohajistudio.tangerine.prototype.global.common.BaseEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@SuperBuilder
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "trending_post")
public class TrendingPost extends BaseEntity {
    @OneToOne
    private Post post;
}
