package io.mohajistudio.tangerine.prototype.domain.post.repository;

import io.mohajistudio.tangerine.prototype.domain.post.domain.FavoritePost;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface FavoritePostRepository extends JpaRepository<FavoritePost, Long> {

    @Query("SELECT fp FROM FavoritePost fp WHERE fp.member.id = :memberId")
    Page<FavoritePost> findByMemberIdForWithdrawal(@Param("memberId") Long memberId, Pageable pageable);

    @Query("SELECT fp FROM FavoritePost fp WHERE fp.member.id = :memberId AND fp.post.id = :postId")
    Optional<FavoritePost> findByMemberIdAndPostId(@Param("memberId") Long memberId, @Param("postId") Long postId);

    @Modifying(clearAutomatically = true)
    @Query("DELETE FROM FavoritePost fp WHERE fp.id = :id")
    void deleteById(@Param("id") Long id);
}
