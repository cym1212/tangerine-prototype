package io.mohajistudio.tangerine.prototype.domain.comment.repository;

import io.mohajistudio.tangerine.prototype.domain.comment.domain.FavoriteComment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.Set;

public interface FavoriteCommentRepository extends JpaRepository<FavoriteComment, Long> {
    @Query("SELECT fc FROM FavoriteComment fc WHERE fc.member.id = :memberId AND fc.comment.id = :commentId")
    Optional<FavoriteComment> findByMemberIdAndCommentId(@Param("memberId") Long memberId, @Param("commentId") Long commentId);

    @Query("SELECT fc FROM FavoriteComment fc WHERE fc.member.id = :memberId AND fc.post.id = :postId")
    Set<FavoriteComment> findByMemberIdAndPostId(@Param("memberId") Long memberId, @Param("postId") Long postId);

    @Query("SELECT fc FROM FavoriteComment fc WHERE fc.member.id = :memberId")
    Page<FavoriteComment> findByMemberIdForWithdrawal(@Param("memberId") Long memberId, Pageable pageable);

    @Modifying(clearAutomatically = true)
    @Query("DELETE FROM FavoriteComment fc WHERE fc.id = :id")
    void deleteById(@Param("id") Long id);
}
