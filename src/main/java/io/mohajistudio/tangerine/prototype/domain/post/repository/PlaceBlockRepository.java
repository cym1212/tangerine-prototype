package io.mohajistudio.tangerine.prototype.domain.post.repository;

import io.mohajistudio.tangerine.prototype.domain.place.domain.Place;
import io.mohajistudio.tangerine.prototype.domain.place.domain.PlaceCategory;
import io.mohajistudio.tangerine.prototype.domain.post.domain.PlaceBlock;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.Optional;

public interface PlaceBlockRepository extends JpaRepository<PlaceBlock, Long> {
    @Modifying
    @Query("update PlaceBlock pb set pb.content = :content, pb.orderNumber = :orderNumber, pb.rating = :rating, pb.placeCategory = :placeCategory, pb.place = :place where pb.id = :id")
    void update(@Param("id") Long id, @Param("content") String content, @Param("orderNumber") short orderNumber, @Param("rating") short rating, @Param("placeCategory") PlaceCategory placeCategory, @Param("place") Place place);

    @Modifying
    @Query("UPDATE PlaceBlock pb SET pb.representativePlaceBlockImageId = :representativePlaceBlockImageId where pb.id = :id")
    void update(@Param("id") Long id, @Param("representativePlaceBlockImageId") Long representativePlaceBlockImageId);

    @Modifying
    @Query("update PlaceBlock pb set pb.deletedAt = :deletedAt where pb.id = :id")
    void delete(@Param("id") Long id, @Param("deletedAt") LocalDateTime deletedAt);

    @Override
    @Query("select pb from PlaceBlock pb " +
            "left join fetch pb.placeCategory " +
            "left join fetch pb.place " +
            "left join fetch pb.placeBlockImages " +
            "where pb.id = :id")
    Optional<PlaceBlock> findById(@Param("id") Long id);

    @Query("SELECT distinct pb from PlaceBlock pb " +
            "LEFT JOIN FETCH pb.placeCategory " +
            "LEFT JOIN FETCH pb.place " +
            "LEFT JOIN FETCH pb.placeBlockImages " +
            "WHERE pb.member.id = :memberId " +
            "ORDER BY pb.createdAt DESC")
    Page<PlaceBlock> findByMemberId(@Param("memberId") Long memberId, Pageable pageable);
}
