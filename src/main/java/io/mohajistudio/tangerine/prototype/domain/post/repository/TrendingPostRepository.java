package io.mohajistudio.tangerine.prototype.domain.post.repository;

import io.mohajistudio.tangerine.prototype.domain.post.domain.TrendingPost;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface TrendingPostRepository extends JpaRepository<TrendingPost, Long> {
    @Override
    @Query("SELECT DISTINCT tp from TrendingPost tp " +
            "JOIN FETCH tp.post p " +
            "JOIN FETCH p.member m " +
            "JOIN FETCH m.memberProfile mp " +
            "WHERE p.deletedAt IS NULL " +
            "ORDER BY tp.id ASC")
    Page<TrendingPost> findAll(Pageable pageable);
}
