package io.mohajistudio.tangerine.prototype.domain.placeblock.repository;

import io.mohajistudio.tangerine.prototype.domain.place.domain.Place;
import io.mohajistudio.tangerine.prototype.domain.place.domain.PlaceCategory;
import io.mohajistudio.tangerine.prototype.domain.placeblock.domain.PlaceBlock;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;

public interface PlaceBlockRepository extends JpaRepository<PlaceBlock, Long> {
    @Modifying
    @Query("update PlaceBlock pb " +
            "set pb.content = :content, " +
            "pb.orderNumber = :orderNumber, " +
            "pb.rating = :rating, " +
            "pb.placeCategory = :placeCategory, " +
            "pb.place = :place, " +
            "pb.visitStartDate = :visitStartDate, " +
            "pb.visitEndDate = :visitEndDate " +
            "where pb.id = :id")
    void update(@Param("id") Long id, @Param("content") String content, @Param("orderNumber") short orderNumber, @Param("rating") short rating, @Param("placeCategory") PlaceCategory placeCategory, @Param("place") Place place, @Param("visitStartDate") LocalDate visitStartDate, @Param("visitEndDate") LocalDate visitEndDate);

    @Modifying
    @Query("UPDATE PlaceBlock pb SET pb.representativePlaceBlockImageId = :representativePlaceBlockImageId where pb.id = :id")
    void update(@Param("id") Long id, @Param("representativePlaceBlockImageId") Long representativePlaceBlockImageId);

    @Modifying
    @Query("update PlaceBlock pb set pb.deletedAt = :deletedAt where pb.id = :id")
    void delete(@Param("id") Long id, @Param("deletedAt") LocalDateTime deletedAt);

    @Override
    @Query("SELECT pb FROM PlaceBlock pb " +
            "LEFT JOIN FETCH pb.placeCategory " +
            "LEFT JOIN FETCH pb.place " +
            "LEFT JOIN FETCH pb.placeBlockImages " +
            "WHERE pb.id = :id")
    Optional<PlaceBlock> findById(@Param("id") Long id);

    @Query("SELECT DISTINCT pb FROM PlaceBlock pb " +
            "LEFT JOIN FETCH pb.placeCategory " +
            "LEFT JOIN FETCH pb.place " +
            "LEFT JOIN pb.placeBlockImages pbi " +
            "WHERE pb.member.id = :memberId " +
            "ORDER BY pb.createdAt DESC")
    Page<PlaceBlock> findByMemberId(@Param("memberId") Long memberId, Pageable pageable);

    @Query("SELECT pb FROM Place pl " +
            "JOIN pl.placeBlocks pb " +
            "LEFT JOIN FETCh pb.placeCategory " +
            "LEFT JOIN FETCh  pb.placeBlockImages " +
            "WHERE pl.id = :placeId")
    Page<PlaceBlock> findAllByPlaceId(@Param("placeId") Long placeId, Pageable pageable);
}
