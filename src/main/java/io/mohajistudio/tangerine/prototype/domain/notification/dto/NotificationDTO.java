package io.mohajistudio.tangerine.prototype.domain.notification.dto;

import io.mohajistudio.tangerine.prototype.domain.comment.dto.CommentDTO;
import io.mohajistudio.tangerine.prototype.domain.member.dto.MemberDTO;
import io.mohajistudio.tangerine.prototype.domain.post.dto.PostDTO;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class NotificationDTO {
    private Long id;

    private LocalDateTime createdAt;

    private String title;

    private String body;

    private boolean read;

    private PostDTO.Notification relatedPost;

    private CommentDTO.Compact relatedComment;

    private MemberDTO relatedMember;
}
