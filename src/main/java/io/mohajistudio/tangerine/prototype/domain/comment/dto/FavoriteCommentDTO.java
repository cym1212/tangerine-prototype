package io.mohajistudio.tangerine.prototype.domain.comment.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FavoriteCommentDTO {
    private Long id;
    private Long memberId;
    private Long commentId;
    private Long postId;
}
