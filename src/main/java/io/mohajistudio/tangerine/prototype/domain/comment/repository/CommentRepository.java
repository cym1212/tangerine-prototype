package io.mohajistudio.tangerine.prototype.domain.comment.repository;

import io.mohajistudio.tangerine.prototype.domain.comment.domain.Comment;
import io.mohajistudio.tangerine.prototype.global.enums.CommentStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.Optional;

public interface CommentRepository extends JpaRepository<Comment, Long> {
    @Query("SELECT c FROM Comment c LEFT JOIN FETCH c.post WHERE c.id = :id")
    Optional<Comment> findById(@Param("id") Long id);

    @Query("SELECT MAX(c.groupNumber) FROM Comment c WHERE c.post.id = :postId")
    Integer findMaxGroupNumberByPostId(@Param("postId") Long postId);

    @Query("SELECT c.groupNumber FROM Comment c WHERE c.id = :id")
    Integer findGroupNumberById(@Param("id") Long id);

    @Query("SELECT c FROM Comment c " +
            "LEFT JOIN FETCH c.member m " +
            "LEFT JOIN FETCH m.memberProfile mp " +
            "LEFT JOIN c.replyComment.member " +
            "LEFT JOIN c.replyComment.member.memberProfile " +
            "WHERE c.post.id = :postId " +
            "ORDER BY c.groupNumber, c.createdAt")
    Page<Comment> findByPostId(@Param("postId") Long postId, Pageable pageable);

    @Modifying(clearAutomatically = true)
    @Query("UPDATE Comment c SET c.modifiedAt = :modifiedAt, c.content = :content WHERE c.id = :id AND c.deletedAt IS NULL")
    void update(@Param("id") Long id, @Param("modifiedAt") LocalDateTime modifiedAt, @Param("content") String content);

    @Modifying(clearAutomatically = true)
    @Query("UPDATE Comment c SET c.deletedAt = :deletedAt, c.status = :commentStatus WHERE c.id =:id AND c.deletedAt IS NULL")
    void delete(@Param("id") Long id, @Param("deletedAt") LocalDateTime deletedAt, @Param("commentStatus") CommentStatus commentStatus);

    @Modifying(clearAutomatically = true)
    @Query("UPDATE Comment c SET c.favoriteCnt = :favoriteCnt WHERE c.id = :id AND c.deletedAt IS NULL")
    void updateFavoriteCnt(@Param("id") Long id, @Param("favoriteCnt") int favoriteCnt);
}
