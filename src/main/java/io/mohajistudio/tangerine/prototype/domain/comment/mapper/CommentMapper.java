package io.mohajistudio.tangerine.prototype.domain.comment.mapper;

import io.mohajistudio.tangerine.prototype.domain.comment.domain.Comment;
import io.mohajistudio.tangerine.prototype.domain.comment.domain.FavoriteComment;
import io.mohajistudio.tangerine.prototype.domain.comment.dto.CommentDTO;
import io.mohajistudio.tangerine.prototype.domain.comment.dto.FavoriteCommentDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;


@Mapper
public interface CommentMapper {
    Comment toEntity(CommentDTO.Add commentAddDTO);

    @Mapping(source = ".", target = "content", qualifiedByName = "checkDeletedComment")
    CommentDTO.Details toDTO(Comment comment);

    Comment toEntity(CommentDTO.Patch commentPatchDTO);

    @Mapping(source = "member.id", target = "memberId")
    @Mapping(source = "post.id", target = "postId")
    @Mapping(source = "comment.id", target = "commentId")
    FavoriteCommentDTO toDTO(FavoriteComment favoriteComment);

    @Named("checkDeletedComment") // 2
    default String convertToDistrict(Comment comment) {
        if (comment.getDeletedAt() != null) {
            return null;
        }
        return comment.getContent();
    }
}
