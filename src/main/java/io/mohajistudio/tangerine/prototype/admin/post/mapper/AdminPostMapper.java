package io.mohajistudio.tangerine.prototype.admin.post.mapper;

import io.mohajistudio.tangerine.prototype.admin.post.dto.AdminPostDTO;
import io.mohajistudio.tangerine.prototype.admin.post.dto.AdminSearchListDTO;
import io.mohajistudio.tangerine.prototype.domain.post.domain.Post;
import org.mapstruct.Mapper;

@Mapper
public interface AdminPostMapper {

    AdminPostDTO.Compact toCompactDTOForAdmin(Post post);

    AdminSearchListDTO toSearchListDTOForAdmin(Post post);
}

