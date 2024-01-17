package io.mohajistudio.tangerine.prototype.domain.post.dto;

import io.mohajistudio.tangerine.prototype.global.enums.ImageMimeType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PlaceBlockImageDTO {
    @NotNull
    @Schema(description = "이미지 경로", example = "https://imageUrl.com")
    private String imageUrl;
    @NotNull
    @Schema(description = "이미지 확장자", example = "image/jpeg")
    private ImageMimeType imageMimeType;
    @NotNull
    @Schema(description = "순서 번호", example = "1")
    private short orderNumber;

    public void setImageMimeType(String imageMimeType) {
        this.imageMimeType = ImageMimeType.fromValue(imageMimeType);
    }

    @Getter
    @Setter
    @Schema(name = "PlaceBlockImageDTO.Details", description = "장소 블럭 이미지의 상세를 반환할 때 사용할 DTO")
    public static class Details extends PlaceBlockImageDTO {
        @Schema(description = "PlaceBlockImage Id", example = "1")
        private Long id;
    }

    @Getter
    @Setter
    @Schema(name = "PlaceBlockImageDTO.Add", description = "장소 블럭 이미지 추가를 위한 DTO")
    public static class Add extends PlaceBlockImageDTO {
    }

    @Getter
    @Setter
    @Schema(name = "PlaceBlockImageDTO.Upload", description = "장소 블럭 이미지 업로드를 위한 DTO")
    public static class Upload extends PlaceBlockImageDTO {
    }

}
