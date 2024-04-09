package io.mohajistudio.tangerine.prototype.domain.comment.dto;

import io.mohajistudio.tangerine.prototype.domain.member.dto.MemberDTO;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class CommentDTO {
    @NotNull
    @Schema(description = "내용", example = "댓글에 들어갈 내용")
    private String content;


    @Getter
    @Setter
    public static class Compact {
        @Schema(description = "Comment Id", example = "1")
        private Long id;
    }

    @Getter
    @Setter
    public static class Reply {
        private Long id;
        private MemberDTO member;
    }

    @Getter
    @Setter
    @Schema(name = "CommentDTO.Add", description = "게시글 추가를 위한 DTO")
    public static class Add extends CommentDTO {
        @Schema(description = "ParentComment Id")
        private CommentDTO.Compact parentComment;
        @Schema(description = "ReplyComment Id")
        private CommentDTO.Compact replyComment;
    }

    @Getter
    @Setter
    @Schema(name = "CommentDTO.Details", description = "댓글의 상세를 반환할 때 사용할 DTO")
    public static class Details extends CommentDTO {
        @Schema(description = "Comment Id", example = "1")
        private Long id;
        @Schema(description = "그룹 번호", example = "1")
        private int groupNumber;
        @Schema(description = "좋아요 개수")
        private int favoriteCnt;
        @Schema(description = "생성 시간")
        private LocalDateTime createdAt;
        @Schema(description = "수정 시간")
        private LocalDateTime modifiedAt;
        @Schema(description = "작성자")
        private MemberDTO member;
        @Schema(description = "ParentComment Id")
        private CommentDTO.Compact parentComment;
        @Schema(description = "ReplyComment")
        private CommentDTO.Reply replyComment;
    }

    @Getter
    @Setter
    public static class Patch extends CommentDTO {
        @Schema(description = "Comment Id", example = "1")
        private Long id;
    }
}
