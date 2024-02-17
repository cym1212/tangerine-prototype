package io.mohajistudio.tangerine.prototype.admin.post.repository;

import io.mohajistudio.tangerine.prototype.domain.post.domain.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;


@Repository
public interface AdminPostRepository extends JpaRepository<Post, Long> {
    Page<Post> findAllByOrderByCreatedAtDesc(Pageable pageable);

    @Query("SELECT p FROM Post p WHERE p.title LIKE %:searchKeyword% OR p.member.memberProfile.nickname LIKE %:searchKeyword% OR p.member.memberProfile.name LIKE %:searchKeyword%")
    Page<Post> findByTitleOrNicknameContaining(String searchKeyword, Pageable pageable);


    Optional<Post> findAdminDetailsById(@Param("id") Long id);

//    @Modifying(clearAutomatically = true)
//    @Query("update Post p set p.deletedAt = NULL where p.id = :id AND p.deletedAt IS NOT NULL")
//    void restoreDelete(@Param("id") Long id, @Param("deletedAt") LocalDateTime deletedAt);
}
