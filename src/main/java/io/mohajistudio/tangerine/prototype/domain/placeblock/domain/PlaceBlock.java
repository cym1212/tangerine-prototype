package io.mohajistudio.tangerine.prototype.domain.placeblock.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.mohajistudio.tangerine.prototype.domain.member.domain.Member;
import io.mohajistudio.tangerine.prototype.domain.place.domain.PlaceCategory;
import io.mohajistudio.tangerine.prototype.domain.placeblockimage.domain.PlaceBlockImage;
import io.mohajistudio.tangerine.prototype.domain.post.domain.Post;
import io.mohajistudio.tangerine.prototype.global.common.BaseEntity;
import io.mohajistudio.tangerine.prototype.domain.place.domain.Place;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.SQLRestriction;

import java.time.LocalDate;
import java.util.Set;

@Getter
@SuperBuilder
@Entity
@AllArgsConstructor
@NoArgsConstructor
@SQLRestriction("deleted_at is NULL")
@Table(name = "place_block")
public class PlaceBlock extends BaseEntity {
    @Column
    private LocalDate visitStartDate;

    @Column
    private LocalDate visitEndDate;

    @Column(nullable = false)
    private short orderNumber;

    @Column(nullable = false, columnDefinition = "TEXT")
    @NotBlank
    private String content;

    @Column(nullable = false)
    private short rating;

    @Setter
    private Long representativePlaceBlockImageId;

    @Setter
    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    private Post post;

    @Setter
    @ManyToOne(optional = false)
    private Place place;

    @Setter
    @ManyToOne(optional = false)
    private PlaceCategory placeCategory;

    @OneToMany(mappedBy = "placeBlock", fetch = FetchType.EAGER)
    private Set<PlaceBlockImage> placeBlockImages;

    @Setter
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    private Member member;

    public void setPlaceBlockImages(Set<PlaceBlockImage> placeBlockImages) {
        this.placeBlockImages = placeBlockImages;
        placeBlockImages.forEach(placeBlockImage -> placeBlockImage.setPlaceBlock(this));
    }

    @Transient
    private short representativePlaceBlockImageOrderNumber;
}