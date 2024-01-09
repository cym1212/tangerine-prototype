package io.mohajistudio.tangerine.prototype.domain.member.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.mohajistudio.tangerine.prototype.domain.comment.domain.Comment;
import io.mohajistudio.tangerine.prototype.domain.comment.domain.FavoriteComment;
import io.mohajistudio.tangerine.prototype.domain.notice.Notification;
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
    private String refreshToken;

    @Column(length = 50, nullable = false, unique = true)
    private String email;

    @Column(length = 10, nullable = false)
    @Enumerated(EnumType.STRING)
    private Role role;

    @Column(length = 10, nullable = false)
    @Enumerated(EnumType.STRING)
    private Provider provider;

    private int followCount = 0; //팔로우 한 멤버 수
    private int followMemberCount = 0; //팔로우 받은 멤버 수

    @OneToOne(mappedBy = "member", fetch = FetchType.LAZY)
    private MemberProfile memberProfile;

    @JsonIgnore
    @OneToMany(mappedBy = "member", fetch = FetchType.LAZY)
    private Set<Notification> notifications;

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

    public static Member createGuest(Provider provider, String email) {
        return Member.builder().provider(provider).role(Role.GUEST).email(email).build();
    }
}
