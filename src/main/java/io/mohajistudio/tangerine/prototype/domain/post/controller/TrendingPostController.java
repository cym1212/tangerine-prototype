package io.mohajistudio.tangerine.prototype.domain.post.controller;

import io.mohajistudio.tangerine.prototype.domain.post.domain.Post;
import io.mohajistudio.tangerine.prototype.domain.post.dto.PostDTO;
import io.mohajistudio.tangerine.prototype.domain.post.mapper.PostMapper;
import io.mohajistudio.tangerine.prototype.domain.post.domain.TrendingPost;
import io.mohajistudio.tangerine.prototype.domain.post.service.TrendingPostService;
import io.mohajistudio.tangerine.prototype.global.common.PageableParam;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/trending-posts")
@RequiredArgsConstructor
@Tag(name = "Trending", description = "Trending API")
public class TrendingPostController {
    private final PostMapper postMapper;
    private final TrendingPostService trendingPostService;

    @GetMapping
    @Operation(summary = "페이징 된 트렌딩 게시글 목록", description = "page와 size 값을 넘기면 페이징 된 게시글 목록을 반환합니다. 기본 값은 page는 1, size는 10 입니다.")
    public Page<PostDTO.Compact> trendingPostListByPage(@ModelAttribute PageableParam pageableParam) {
        Pageable pageable = PageRequest.of(pageableParam.getPage(), pageableParam.getSize());

        Page<TrendingPost> trendingPostListByPage = trendingPostService.findTrendingPostListByPage(pageable);
        Page<Post> postListWithPagination = trendingPostListByPage.map(TrendingPost::getPost);
        return postListWithPagination.map(postMapper::toCompactDTO);
    }

}
