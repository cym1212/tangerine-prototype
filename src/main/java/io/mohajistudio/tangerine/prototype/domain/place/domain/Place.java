package io.mohajistudio.tangerine.prototype.domain.place.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.mohajistudio.tangerine.prototype.domain.post.domain.PlaceBlock;
import io.mohajistudio.tangerine.prototype.global.common.BaseEntity;
import io.mohajistudio.tangerine.prototype.global.enums.PlaceProvider;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import org.locationtech.jts.geom.Point;
import org.springframework.data.domain.Persistable;

import java.util.List;

@Getter
@SuperBuilder
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "place")
public class Place extends BaseEntity implements Persistable<Long> {
    @Column(nullable = false)
    private String name;
    @Column(nullable = false, columnDefinition = "geometry(Point, 4326)")
    private Point coordinate;
    private String thumbnail;
    @Column(columnDefinition = "varchar(20)", nullable = false)
    private String addressProvince;//광역시/도
    @Column(columnDefinition = "varchar(20)", nullable = false)
    private String addressCity;//시/군/구
    @Column(columnDefinition = "varchar(20)", nullable = false)
    private String addressDistrict;//읍/면/동
    @Column(columnDefinition = "varchar(20)")
    private String addressDetail;//이하
    private String roadAddress;
    @Column(length = 500)
    private String description;
    private String link;
    @Setter
    @Column(columnDefinition = "varchar(10)", nullable = false)
    @Enumerated(EnumType.STRING)
    private PlaceProvider placeSearchProvider;
    @Setter
    @Column(unique = true)
    private Long providerId;

    @JsonIgnore
    @OneToMany(mappedBy = "place")
    private List<PlaceBlock> placeBlocks;

    public String getAddress() {
        return getAddressProvince() + " " + getAddressCity() + " " + getAddressDistrict() + " " + getAddressDetail();
    }

    @Override
    public boolean isNew() {
        return getId() == null && getCreatedAt() == null;
    }
}