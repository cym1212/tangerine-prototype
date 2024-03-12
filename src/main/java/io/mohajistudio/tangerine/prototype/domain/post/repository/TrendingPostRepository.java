package io.mohajistudio.tangerine.prototype.domain.post.repository;

import io.mohajistudio.tangerine.prototype.domain.post.domain.TrendingPost;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TrendingPostRepository extends JpaRepository<TrendingPost, Long> {
}
