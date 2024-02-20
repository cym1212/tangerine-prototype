package io.mohajistudio.tangerine.prototype.domain.post.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.mohajistudio.tangerine.prototype.domain.comment.domain.Comment;
import io.mohajistudio.tangerine.prototype.domain.comment.domain.FavoriteComment;
import io.mohajistudio.tangerine.prototype.domain.member.domain.Member;
import io.mohajistudio.tangerine.prototype.global.common.BaseEntity;
import io.mohajistudio.tangerine.prototype.global.enums.PostStatus;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.time.LocalDate;
import java.util.Set;

@Getter
@Entity
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@Table(name = "post")
public class Post extends BaseEntity {
    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private LocalDate visitStartDate;

    @Column(nullable = false)
    private LocalDate visitEndDate;

    @Column(nullable = false)
    @Setter
    private int commentCnt = 0;

    @Column(nullable = false)
    private int favoriteCnt = 0;

    @Setter
    private short placeBlockCnt = 0;

    @Setter
    private String thumbnail;

    //방문 지역
    @Column(nullable = false)
    private String visitRegion;

    @Transient
    @Setter
    private Boolean isFavorite;

    @Setter
    @Column(nullable = false, length = 10)
    @Enumerated(EnumType.STRING)
    private PostStatus status;

    @Setter
    @ManyToOne(fetch = FetchType.LAZY)
    private Member member;

    @JsonIgnore
    @OneToMany(mappedBy = "post", fetch = FetchType.LAZY)
    private Set<ScrapPost> scrapPosts;

    @JsonIgnore
    @OneToMany(mappedBy = "post", fetch = FetchType.LAZY)
    private Set<FavoritePost> favoritePosts;

    @JsonIgnore
    @OneToMany(mappedBy = "post", fetch = FetchType.LAZY)
    private Set<Comment> comments;

    @OneToMany(mappedBy = "post", fetch = FetchType.LAZY)
    private Set<TextBlock> textBlocks;

    @OneToMany(mappedBy = "post", fetch = FetchType.LAZY)
    private Set<PlaceBlock> placeBlocks;

    @OneToMany(mappedBy = "post", fetch = FetchType.LAZY)
    private Set<FavoriteComment> favoriteComments;
}
