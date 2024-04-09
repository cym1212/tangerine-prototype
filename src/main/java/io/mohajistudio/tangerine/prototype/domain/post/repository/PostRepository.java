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
    @Query("select p from Post p where p.id = :id")
    Optional<Post> findById(@Param("id") Long id);

    @Query("select p from Post p " +
            "LEFT JOIN FETCH p.member m " +
            "LEFT JOIN FETCH m.memberProfile mp " +
            "WHERE p.id = :id")
    Optional<Post> findByIdWithMember(@Param("id") Long id);

    @Query("SELECT p " +
            "FROM Post p " +
            "JOIN FETCH p.member m " +
            "JOIN FETCH m.memberProfile mp " +
            "LEFT JOIN FETCH p.textBlocks " +
            "LEFT JOIN FETCH p.placeBlocks pb " +
            "LEFT JOIN FETCH pb.placeBlockImages pbi " +
            "LEFT JOIN FETCH pb.placeCategory c " +
            "LEFT JOIN FETCH pb.place pl " +
            "WHERE p.id = :id " +
            "ORDER BY pbi.orderNumber ASC"
    )
    Optional<Post> findByIdDetails(@Param("id") Long id);

    @Override
    @Query("SELECT DISTINCT p from Post p " +
            "JOIN FETCH p.member m " +
            "JOIN FETCH m.memberProfile mp " +
            "ORDER BY p.id DESC")
    Page<Post> findAll(Pageable pageable);

    @Query("SELECT DISTINCT p FROM Post p " +
            "JOIN FETCH p.member m " +
            "JOIN FETCH m.memberProfile mp " +
            "WHERE p.title ILIKE %:keyword%")
    Page<Post> findAllContainingKeyword(Pageable pageable, @Param("keyword") String keyword);

    @Query("SELECT DISTINCT p FROM Post p " +
            "JOIN FETCH p.member m " +
            "JOIN FETCH m.memberProfile mp " +
            "WHERE p.member.id = :memberId " +
            "ORDER BY p.createdAt DESC")
    Page<Post> findByMemberId(@Param("memberId") Long memberId, Pageable pageable);

    @Query("SELECT p FROM Post p WHERE p.member.id = :memberId AND p.createdAt >= :dateTime ORDER BY p.createdAt DESC")
    List<Post> findAllByMemberIdAfter(@Param("memberId") Long memberId, @Param("dateTime") LocalDateTime dateTime);

    @Modifying(clearAutomatically = true)
    @Query("update Post p set p.favoriteCnt = :favoriteCnt where p.id = :id")
    void updateFavoriteCnt(@Param("id") Long id, @Param("favoriteCnt") int favoriteCnt);

    @Modifying(clearAutomatically = true)
    @Query("update Post p set p.commentCnt = :commentCnt where p.id = :id")
    void updateCommentCnt(@Param("id") Long id, @Param("commentCnt") int commentCnt);

    @Modifying(clearAutomatically = true)
    @Query("update Post p set p.modifiedAt = :modifiedAt, p.title = :title, p.visitStartDate = :visitStartDate, p.visitEndDate = :visitEndDate, p.placeBlockCnt = :placeBlockCnt, p.thumbnail = :thumbnail where p.id = :id")
    void update(@Param("id") Long id, @Param("modifiedAt") LocalDateTime modifiedAt, @Param("title") String title, @Param("visitStartDate") LocalDate visitStartDate, @Param("visitEndDate") LocalDate visitEndDate, @Param("placeBlockCnt") short placeBlockCnt, @Param("thumbnail") String thumbnail);

    @Modifying(clearAutomatically = true)
    @Query("update Post p set p.deletedAt = :deletedAt, p.status = :postStatus where p.id = :id")
    void delete(@Param("id") Long id, @Param("deletedAt") LocalDateTime deletedAt, @Param("postStatus") PostStatus postStatus);
}
