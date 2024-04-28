package io.mohajistudio.tangerine.prototype.domain.notification.domain;

import io.mohajistudio.tangerine.prototype.domain.comment.domain.Comment;
import io.mohajistudio.tangerine.prototype.domain.member.domain.Member;
import io.mohajistudio.tangerine.prototype.domain.post.domain.Post;
import io.mohajistudio.tangerine.prototype.global.common.BaseEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.HashMap;
import java.util.Map;

@Getter
@Builder
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "notification")
public class Notification extends BaseEntity {
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    private Member member;

    private String title;

    private String body;

    @Column(nullable = false)
    private boolean read = false;

    @ManyToOne(fetch = FetchType.LAZY)
    private Post relatedPost;

    @ManyToOne(fetch = FetchType.LAZY)
    private Comment relatedComment;

    @ManyToOne(fetch = FetchType.LAZY)
    private Member relatedMember;

    public Map<String, String> getData() {
        Map<String, String> data = new HashMap<>();
        data.put("member", member.getId().toString());
        data.put("relatedPost", relatedPost.getId().toString());
        data.put("relatedComment", relatedComment.getId().toString());
        data.put("relatedMember", relatedMember.getId().toString());
        return data;
    }
}
