package io.mohajistudio.tangerine.prototype.domain.place.repository;

import io.mohajistudio.tangerine.prototype.domain.place.domain.Place;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface PlaceRepository extends JpaRepository<Place, Long> {
    @Query("SELECT p FROM Place p WHERE p.name LIKE %:query%")
    Page<Place> findByName(@Param("query") String query, Pageable pageable);

    @Query("SELECT p FROM Place p WHERE p.providerId = :providerId")
    Optional<Place> findByProviderId(@Param("providerId") Long providerId);

    @Modifying(clearAutomatically = true)
    @Query(value = "UPDATE Place SET " +
            "name = :name, " +
            "coordinate = ST_SetSRID(ST_MakePoint(:lat, :lng), 4326), " +
            "address_province = :addressProvince, " +
            "address_city = :addressCity, " +
            "address_district = :addressDistrict, " +
            "address_detail = :addressDetail, " +
            "road_address = :roadAddress, " +
            "description = :description, " +
            "link = :link " +
            "WHERE id = :id", nativeQuery = true)
    void update(@Param("id") Long id, @Param("name") String name, @Param("lat") double lat, @Param("lng") double lng, @Param("addressProvince") String addressProvince, @Param("addressCity") String addressCity, @Param("addressDistrict") String addressDistrict, @Param("addressDetail") String addressDetail, @Param("roadAddress") String roadAddress, @Param("description") String description, @Param("link") String link);
}
