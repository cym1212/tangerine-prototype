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
    @Query("SELECT c FROM Comment c WHERE c.id = :id")
    Optional<Comment> findByIdForWithdrawal(@Param("id") Long id);

    @Query("SELECT c FROM Comment c LEFT JOIN FETCH c.post WHERE c.id = :id")
    Optional<Comment> findById(@Param("id") Long id);

    @Query("SELECT c FROM Comment c " +
            "LEFT JOIN FETCH c.member m " +
            "LEFT JOIN FETCH m.memberProfile mp " +
            "WHERE c.id = :id AND c.post.id = :postId")
    Optional<Comment> findByIdDetails(@Param("id") Long id, @Param("postId") Long postId);

    @Query("SELECT c FROM Comment c " +
            "LEFT JOIN FETCH c.member m " +
            "LEFT JOIN FETCH m.memberProfile mp " +
            "WHERE c.id = :id")
    Optional<Comment> findByIdWithMember(@Param("id") Long id);

    @Query("SELECT MAX(c.groupNumber) FROM Comment c WHERE c.post.id = :postId")
    Integer findMaxGroupNumberByPostId(@Param("postId") Long postId);

    @Query("SELECT c FROM Comment c " +
            "LEFT JOIN FETCH c.member m " +
            "LEFT JOIN FETCH m.memberProfile mp " +
            "LEFT JOIN c.replyComment.member " +
            "LEFT JOIN c.replyComment.member.memberProfile " +
            "WHERE c.post.id = :postId " +
            "ORDER BY c.groupNumber DESC, c.id DESC")
    Page<Comment> findByPostIdOrderByDesc(@Param("postId") Long postId, Pageable pageable);

    @Query("SELECT c FROM Comment c " +
            "LEFT JOIN FETCH c.member m " +
            "LEFT JOIN FETCH m.memberProfile mp " +
            "LEFT JOIN c.replyComment.member " +
            "LEFT JOIN c.replyComment.member.memberProfile " +
            "WHERE c.post.id = :postId " +
            "ORDER BY c.groupNumber ASC, c.id ASC")
    Page<Comment> findByPostIdOrderByAsc(@Param("postId") Long postId, Pageable pageable);

    @Query("SELECT c FROM Comment c " +
            "LEFT JOIN FETCH c.member m " +
            "LEFT JOIN FETCH m.memberProfile mp " +
            "LEFT JOIN c.replyComment.member " +
            "LEFT JOIN c.replyComment.member.memberProfile " +
            "WHERE c.post.id = :postId " +
            "AND c.parentComment.id = :commentId " +
            "ORDER BY c.id DESC")
    Page<Comment> findByPostIdAndCommentIdOrderByDesc(@Param("postId") Long postId, @Param("commentId") Long commentId, Pageable pageable);

    @Query("SELECT c FROM Comment c " +
            "LEFT JOIN FETCH c.member m " +
            "LEFT JOIN FETCH m.memberProfile mp " +
            "LEFT JOIN c.replyComment.member " +
            "LEFT JOIN c.replyComment.member.memberProfile " +
            "WHERE c.post.id = :postId " +
            "AND c.parentComment.id = :commentId " +
            "ORDER BY c.id ASC")
    Page<Comment> findByPostIdAndCommentIdOrderByAsc(@Param("postId") Long postId, @Param("commentId") Long commentId, Pageable pageable);

    @Modifying(clearAutomatically = true)
    @Query("UPDATE Comment c SET c.modifiedAt = :modifiedAt, c.content = :content WHERE c.id = :id")
    void update(@Param("id") Long id, @Param("modifiedAt") LocalDateTime modifiedAt, @Param("content") String content);

    @Modifying(clearAutomatically = true)
    @Query("UPDATE Comment c SET c.deletedAt = :deletedAt, c.status = :commentStatus WHERE c.id =:id")
    void delete(@Param("id") Long id, @Param("deletedAt") LocalDateTime deletedAt, @Param("commentStatus") CommentStatus commentStatus);

    @Modifying(clearAutomatically = true)
    @Query("UPDATE Comment c SET c.favoriteCnt = :favoriteCnt WHERE c.id = :id")
    void updateFavoriteCnt(@Param("id") Long id, @Param("favoriteCnt") int favoriteCnt);

    @Modifying(clearAutomatically = true)
    @Query("UPDATE Comment c SET c.member = null, c.deletedAt = :deletedAt WHERE c.id = :commentId ")
    void permanentDelete(@Param("commentId") Long commentId, @Param("deletedAt") LocalDateTime deletedAt);

    @Query("SELECT c FROM Comment c WHERE c.member.id = :memberId ")
    Page<Comment> findByMemberIdForWithdrawal(@Param("memberId") Long memberId, Pageable pageable);


}