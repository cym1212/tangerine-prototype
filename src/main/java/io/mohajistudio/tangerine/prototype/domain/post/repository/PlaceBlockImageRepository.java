package io.mohajistudio.tangerine.prototype.domain.post.repository;

import io.mohajistudio.tangerine.prototype.domain.placeblockimage.domain.PlaceBlockImage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;

public interface PlaceBlockImageRepository extends JpaRepository<PlaceBlockImage, Long> {
    @Modifying
    @Query("update PlaceBlockImage pbi set pbi.storageKey = :storageKey, pbi.orderNumber = :orderNumber where pbi.id = :id")
    void update(@Param("id") Long id, @Param("storageKey") String content, @Param("orderNumber") short orderNumber);

    @Modifying
    @Query("update PlaceBlockImage pbi set pbi.deletedAt = :deletedAt where pbi.id = :id")
    void delete(@Param("id") Long id, @Param("deletedAt") LocalDateTime deletedAt);
}
