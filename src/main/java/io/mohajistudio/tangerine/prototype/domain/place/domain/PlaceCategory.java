package io.mohajistudio.tangerine.prototype.domain.place.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.mohajistudio.tangerine.prototype.domain.post.domain.PlaceBlock;
import io.mohajistudio.tangerine.prototype.global.common.BaseEntity;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.util.Set;

@Getter
@SuperBuilder
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "place_category")
public class PlaceCategory extends BaseEntity {
    @Column(nullable = false, length = 20)
    private String name;

    @JsonIgnore
    @OneToMany(mappedBy = "placeCategory")
    private Set<PlaceBlock> placeBlocks;
}
