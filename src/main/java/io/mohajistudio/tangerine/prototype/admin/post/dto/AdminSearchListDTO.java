package io.mohajistudio.tangerine.prototype.admin.post.dto;


import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AdminSearchListDTO extends AdminPostDTO{
    private String searchKeyword;
}