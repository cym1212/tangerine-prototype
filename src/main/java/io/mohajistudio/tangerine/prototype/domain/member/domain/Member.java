package io.mohajistudio.tangerine.prototype.domain.member.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.mohajistudio.tangerine.prototype.domain.comment.domain.Comment;
import io.mohajistudio.tangerine.prototype.domain.comment.domain.FavoriteComment;
import io.mohajistudio.tangerine.prototype.domain.notification.domain.Notification;
import io.mohajistudio.tangerine.prototype.domain.placeblock.domain.PlaceBlock;
import io.mohajistudio.tangerine.prototype.domain.post.domain.*;
import io.mohajistudio.tangerine.prototype.global.common.BaseEntity;
import io.mohajistudio.tangerine.prototype.global.enums.Provider;
import io.mohajistudio.tangerine.prototype.global.enums.Role;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.util.Set;

@SuperBuilder
@Getter
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "member")
public class Member extends BaseEntity {
    @Setter
    private String refreshToken;

    @Column(length = 50, nullable = false, unique = true)
    private String email;

    @Column(length = 10, nullable = false)
    @Enumerated(EnumType.STRING)
    private Role role;

    @Column(length = 10, nullable = false)
    @Enumerated(EnumType.STRING)
    private Provider provider;

    @Column(unique = true, nullable = false)
    private String providerId;

    private String notificationToken;

    private int followCnt = 0; //팔로우 한 멤버 수

    private int followMemberCnt = 0; //팔로우 받은 멤버 수

    @Setter
    @Column(nullable = false)
    private int unreadNotificationsCnt = 0;

    @OneToOne(mappedBy = "member", fetch = FetchType.LAZY)
    private MemberProfile memberProfile;

    @OneToMany(mappedBy = "member", fetch = FetchType.LAZY)
    private Set<Notification> notifications;

    @JsonIgnore
    @OneToMany(mappedBy = "relatedMember", fetch = FetchType.LAZY)
    private Set<Notification> relatedNotifications;

    @JsonIgnore
    @OneToMany(mappedBy = "member", fetch = FetchType.LAZY)
    private Set<Post> posts;

    @JsonIgnore
    @OneToMany(mappedBy = "member", fetch = FetchType.LAZY)
    private Set<Comment> comments;

    @JsonIgnore
    @OneToMany(mappedBy = "member", fetch = FetchType.LAZY)
    private Set<FavoriteComment> favoriteComments;

    @JsonIgnore
    @OneToMany(mappedBy = "member", fetch = FetchType.LAZY)
    private Set<Follow> follows;

    @JsonIgnore
    @OneToMany(mappedBy = "followMember", fetch = FetchType.LAZY)
    private Set<Follow> followMembers;

    @JsonIgnore
    @OneToMany(mappedBy = "member", fetch = FetchType.LAZY)
    private Set<ScrapPost> scrapPosts;

    @JsonIgnore
    @OneToMany(mappedBy = "member", fetch = FetchType.LAZY)
    private Set<FavoritePost> favoritePosts;

    @OneToMany(mappedBy = "member", fetch = FetchType.LAZY)
    private Set<PlaceBlock> placeBlocks;

    public static Member createGuest(Provider provider, String providerId, String email) {
        return Member.builder().provider(provider).role(Role.GUEST).email(email).providerId(providerId).build();
    }
}
