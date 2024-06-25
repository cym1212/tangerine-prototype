package io.mohajistudio.tangerine.prototype.domain.comment.domain;

import io.mohajistudio.tangerine.prototype.domain.member.domain.Member;
import io.mohajistudio.tangerine.prototype.domain.notification.domain.Notification;
import io.mohajistudio.tangerine.prototype.domain.post.domain.Post;
import io.mohajistudio.tangerine.prototype.global.common.BaseEntity;
import io.mohajistudio.tangerine.prototype.global.enums.CommentStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.util.Set;

@Getter
@SuperBuilder
@Entity
@AllArgsConstructor
@NoArgsConstructor

@Table(name = "comment")
public class Comment extends BaseEntity {
    @Setter
    @Column(nullable = false)
    private String content;

    @Setter
    @Column(nullable = false)
    private int groupNumber;

    @Column(nullable = false)
    private int favoriteCnt = 0;

    @Setter
    @Column(nullable = false, length = 10)
    @Enumerated(EnumType.STRING)
    private CommentStatus status;

    @Setter
    @ManyToOne(fetch = FetchType.LAZY)
    private Member member;

    @Setter
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    private Post post;

    @Setter
    @ManyToOne(fetch = FetchType.LAZY)
    private Comment parentComment;

    @OneToMany(mappedBy = "parentComment")
    private Set<Comment> childComments;

    @Setter
    @ManyToOne(fetch = FetchType.LAZY)
    private Comment replyComment;

    @OneToMany(mappedBy = "replyComment", fetch = FetchType.LAZY)
    private Set<Comment> repliedComments;

    @OneToMany(mappedBy = "comment", fetch = FetchType.LAZY)
    private Set<FavoriteComment> favoriteComments;

    @OneToMany(mappedBy = "relatedComment", fetch = FetchType.LAZY)
    private Set<Notification> relatedNotifications;
}
