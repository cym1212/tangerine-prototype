package io.mohajistudio.tangerine.prototype.domain.post.dto;

import io.mohajistudio.tangerine.prototype.domain.member.dto.MemberDTO;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Set;

@Getter
@Setter
public class PostDTO {
    @NotNull
    @Schema(description = "게시글의 제목", example = "테스트 제목")
    @Length(min = 5, max = 100)
    private String title;

    @NotNull
    @PastOrPresent
    @Schema(description = "방문 시작 날짜")
    private LocalDate visitStartDate;

    @NotNull
    @PastOrPresent
    @Schema(description = "방문 도착 날짜")
    private LocalDate visitEndDate;

    @NotNull
    @NotEmpty
    private String visitRegion;

    @NotNull
    @NotEmpty
    private String thumbnail;

    @Getter
    @Setter
    @Schema(name = "PostDTO.Details", description = "게시글의 상세를 반환할 때 사용할 DTO")
    public static class Details extends PostDTO.Compact {
        @Schema(description = "작성 시간", example = "2023-02-17T11:44:30.327959")
        private LocalDateTime createdAt;
        @Schema(description = "수정 시간", example = "2023-02-17T11:44:30.327959")
        private LocalDateTime modifiedAt;
        @Schema(description = "좋아하는 게시글 여부", example = "true")
        private Boolean isFavorite;

        @Valid
        @ArraySchema(arraySchema = @Schema(description = "텍스트 블럭"))
        private Set<TextBlockDTO.Details> textBlocks;
        @Valid
        @ArraySchema(arraySchema = @Schema(description = "장소 블럭"))
        private Set<PlaceBlockDTO.Details> placeBlocks;
    }

    @Getter
    @Setter
    @Schema(name = "PostDTO.Add", description = "게시글 추가를 위한 DTO")
    public static class Add extends PostDTO {
        @Valid
        @NotNull
        @ArraySchema(arraySchema = @Schema(description = "텍스트 블럭"))
        private Set<TextBlockDTO.Add> textBlocks;
        @Valid
        @NotNull
        @ArraySchema(arraySchema = @Schema(description = "장소 블럭"))
        private Set<PlaceBlockDTO.Add> placeBlocks;
    }

    @Getter
    @Setter
    @Schema(name = "PostDTO.Details", description = "게시글 목록을 반환할 때 사용할 DTO")
    public static class Compact extends PostDTO {
        @Schema(description = "Post Id", example = "1")
        private Long id;
        @Schema(description = "댓글 개수")
        private int commentCnt;
        @Schema(description = "좋아요 개수")
        private int favoriteCnt;
        @Schema(description = "블럭 개수")
        private short placeBlockCnt;
        @Schema(description = "작성자")
        private MemberDTO member;
    }
}
