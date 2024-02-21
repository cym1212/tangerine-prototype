package io.mohajistudio.tangerine.prototype.domain.post.repository;

import io.mohajistudio.tangerine.prototype.domain.post.domain.Post;
import io.mohajistudio.tangerine.prototype.global.enums.PostStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface PostRepository extends JpaRepository<Post, Long> {
    @Override
    @Query("select p from Post p where p.id = :id and p.deletedAt IS NULL")
    Optional<Post> findById(@Param("id") Long id);

    @Query("select distinct p from Post p " +
            "join fetch p.member m " +
            "join fetch m.memberProfile mp " +
            "left join fetch p.textBlocks tb " +
            "left join fetch p.placeBlocks pb " +
            "left join fetch pb.placeBlockImages pbi " +
            "left join fetch pb.placeCategory c " +
            "left join fetch pb.place pl " +
            "where p.id = :id " +
            "and p.deletedAt IS NULL " +
            "and tb.deletedAt IS NULL " +
            "and pb.deletedAt IS NULL"
    )
    Optional<Post> findByIdDetails(@Param("id") Long id);

    @Override
    @Query("select distinct p from Post p " +
            "join fetch p.member m " +
            "join fetch m.memberProfile mp " +
            "where p.deletedAt IS NULL")
    Page<Post> findAll(Pageable pageable);

    @Query("select distinct p from Post p " +
            "join fetch p.member m " +
            "join fetch m.memberProfile mp " +
            "WHERE p.deletedAt IS NULL " +
            "AND p.member.id = :memberId")
    Page<Post> findByMemberId(@Param("memberId") Long memberId, Pageable pageable);

    @Query("SELECT p FROM Post p WHERE p.member.id = :memberId AND p.createdAt >= :twentyFourHoursAgo ORDER BY p.createdAt DESC")
    List<Post> countPostsToday(@Param("memberId") Long memberId, @Param("twentyFourHoursAgo") LocalDateTime twentyFourHoursAgo);

    @Modifying(clearAutomatically = true)
    @Query("update Post p set p.favoriteCnt = :favoriteCnt where p.id = :id and p.deletedAt IS NULL")
    void updateFavoriteCnt(@Param("id") Long id, @Param("favoriteCnt") int favoriteCnt);

    @Modifying(clearAutomatically = true)
    @Query("update Post p set p.commentCnt = :commentCnt where p.id = :id and p.deletedAt IS NULL")
    void updateCommentCnt(@Param("id") Long id, @Param("commentCnt") int commentCnt);

    @Modifying(clearAutomatically = true)
    @Query("update Post p set p.title = :title, p.visitStartDate = :visitStartDate, p.visitEndDate = :visitEndDate, p.placeBlockCnt = :placeBlockCnt where p.id = :id and p.deletedAt IS NULL")
    void update(@Param("id") Long id, @Param("title") String title, @Param("visitStartDate") LocalDate visitStartDate, @Param("visitEndDate") LocalDate visitEndDate, @Param("placeBlockCnt") short placeBlockCnt);

    @Modifying(clearAutomatically = true)
    @Query("update Post p set p.deletedAt = :deletedAt, p.status = :postStatus where p.id = :id and p.deletedAt IS NULL")
    void delete(@Param("id") Long id, @Param("deletedAt") LocalDateTime deletedAt, @Param("postStatus") PostStatus postStatus);
}
