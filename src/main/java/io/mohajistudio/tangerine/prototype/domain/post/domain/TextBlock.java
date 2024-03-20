package io.mohajistudio.tangerine.prototype.domain.post.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.mohajistudio.tangerine.prototype.global.common.BaseEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.SQLRestriction;

@Getter
@SuperBuilder
@Entity
@AllArgsConstructor
@NoArgsConstructor
@SQLRestriction("deleted_at is NULL")
@Table(name = "text_block")
public class TextBlock extends BaseEntity {

    @Column(nullable = false)
    private short orderNumber;

    @Column(nullable = false, columnDefinition = "TEXT")
    @NotBlank
    private String content;

    @Setter
    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    private Post post;
}
