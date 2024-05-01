package io.mohajistudio.tangerine.prototype.domain.post.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.mohajistudio.tangerine.prototype.domain.comment.domain.Comment;
import io.mohajistudio.tangerine.prototype.domain.comment.domain.FavoriteComment;
import io.mohajistudio.tangerine.prototype.domain.member.domain.Member;
import io.mohajistudio.tangerine.prototype.domain.notification.domain.Notification;
import io.mohajistudio.tangerine.prototype.domain.placeblock.domain.PlaceBlock;
import io.mohajistudio.tangerine.prototype.global.common.BaseEntity;
import io.mohajistudio.tangerine.prototype.global.enums.ErrorCode;
import io.mohajistudio.tangerine.prototype.global.enums.PostStatus;
import io.mohajistudio.tangerine.prototype.global.error.exception.BusinessException;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.SQLRestriction;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Getter
@Entity
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@SQLRestriction("status = 'PUBLISHED'")
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

    @Column(nullable = false)
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

    @OneToMany(mappedBy = "relatedPost", fetch = FetchType.LAZY)
    private Set<Notification> relatedNotifications;

    public void validate() {
        setVisitDate();
        setPlaceBlockCnt();
        checkBlockOrderNumberAndContentIsEmpty();
    }

    private void setVisitDate() {
        visitStartDate = null;
        visitEndDate = null;

        placeBlocks.forEach(placeBlock -> {
            if (placeBlock.getVisitStartDate().isAfter(placeBlock.getVisitEndDate())) {
                throw new BusinessException(ErrorCode.INVALID_DATE_RANGE);
            }

            if (visitStartDate == null && visitEndDate == null) {
                visitStartDate = placeBlock.getVisitStartDate();
                visitEndDate = placeBlock.getVisitEndDate();
                return;
            }

            if (placeBlock.getVisitStartDate().isBefore(visitStartDate)) {
                visitStartDate = placeBlock.getVisitStartDate();
            }

            if (placeBlock.getVisitEndDate().isAfter(visitEndDate)) {
                visitEndDate = placeBlock.getVisitEndDate();
            }
        });
    }

    private void setPlaceBlockCnt() {
        placeBlockCnt = (short) getPlaceBlocks().size();
    }

    private void checkBlockOrderNumberAndContentIsEmpty() {
        Set<Short> orderNumbers = new HashSet<>();

        placeBlocks.forEach(placeBlock -> {
            if (!orderNumbers.add(placeBlock.getOrderNumber())) {
                throw new BusinessException(ErrorCode.INVALID_ORDER_NUMBER);
            }
            if (placeBlock.getContent().isEmpty()) {
                throw new BusinessException(ErrorCode.CONTENT_IS_EMPTY);
            }
        });
        textBlocks.forEach(textBlock -> {
            if (!orderNumbers.add(textBlock.getOrderNumber())) {
                throw new BusinessException(ErrorCode.INVALID_ORDER_NUMBER);
            }
            if (textBlock.getContent().isEmpty()) {
                throw new BusinessException(ErrorCode.CONTENT_IS_EMPTY);
            }
        });

        int totalSize = placeBlocks.size() + textBlocks.size();
        for (short i = 1; i <= totalSize; i++) {
            if (!orderNumbers.contains(i)) {
                throw new BusinessException(ErrorCode.INVALID_ORDER_NUMBER);
            }
        }
    }
}
