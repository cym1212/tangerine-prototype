package io.mohajistudio.tangerine.prototype.admin.post.repository;

import io.mohajistudio.tangerine.prototype.domain.post.domain.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;

@Repository
public interface AdminPostRepository extends JpaRepository<Post, Long> {
    Page<Post> findAllByOrderByCreatedAtDesc(Pageable pageable);

    Page<Post> findByTitleContaining(String searchKeyword, Pageable pageable);

//    @Modifying(clearAutomatically = true)
//    @Query("update Post p set p.deletedAt = NULL where p.id = :id AND p.deletedAt IS NOT NULL")
//    void restoreDelete(@Param("id") Long id, @Param("deletedAt") LocalDateTime deletedAt);
}
