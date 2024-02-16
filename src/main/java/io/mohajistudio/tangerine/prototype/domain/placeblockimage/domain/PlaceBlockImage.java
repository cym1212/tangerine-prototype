package io.mohajistudio.tangerine.prototype.domain.placeblockimage.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.mohajistudio.tangerine.prototype.domain.post.domain.PlaceBlock;
import io.mohajistudio.tangerine.prototype.domain.post.domain.Post;
import io.mohajistudio.tangerine.prototype.global.common.BaseEntity;
import io.mohajistudio.tangerine.prototype.global.enums.ImageMimeType;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.util.Set;

@Getter
@SuperBuilder
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "place_block_image")
public class PlaceBlockImage extends BaseEntity {

    @Setter
    @Column(nullable = false)
    private String imageUrl;

    @Column(nullable = false, length = 10)
    @Enumerated(EnumType.STRING)
    private ImageMimeType imageMimeType;

    @Column(nullable = false)
    private short orderNumber;

    @Setter
    @JsonIgnore
    @ManyToOne(optional = false)
    private PlaceBlock placeBlock;

    public void setImageMimeType(String imageMimeType) {
        this.imageMimeType = ImageMimeType.fromValue(imageMimeType);
    }
}
