package io.mohajistudio.tangerine.prototype.domain.placeblockimage.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.mohajistudio.tangerine.prototype.domain.post.domain.PlaceBlock;
import io.mohajistudio.tangerine.prototype.global.common.BaseEntity;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

@Getter
@SuperBuilder
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "place_block_image")
public class PlaceBlockImage extends BaseEntity {

    @Setter
    @Column(nullable = false)
    private String storageKey;

    @Column(nullable = false)
    private short orderNumber;

    @Setter
    @JsonIgnore
    @ManyToOne(optional = false)
    private PlaceBlock placeBlock;
}
