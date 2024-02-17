package io.mohajistudio.tangerine.prototype.admin.post.controller;


import io.mohajistudio.tangerine.prototype.admin.post.dto.AdminPostDTO;
import io.mohajistudio.tangerine.prototype.admin.post.mapper.AdminPostMapper;
import io.mohajistudio.tangerine.prototype.admin.post.service.AdminPostService;
import io.mohajistudio.tangerine.prototype.domain.post.domain.Post;
import io.mohajistudio.tangerine.prototype.global.common.PageableParam;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;


@Controller
@RequestMapping("/admin/posts")
@RequiredArgsConstructor
@Slf4j
public class AdminPostController {

    private final AdminPostService adminPostService;
    private final AdminPostMapper adminPostMapper;


    @GetMapping
    public ModelAndView postListByPage(@ModelAttribute PageableParam pageableParam) {
        Pageable pageable = PageRequest.of(pageableParam.getPage(), pageableParam.getSize(), Sort.by(Sort.Direction.DESC, "createdAt"));

        Page<AdminPostDTO.Compact> adminPostDTOPage = adminPostService.adminPostList(pageable)
                .map(adminPostMapper::toCompactDTOForAdmin);

        ModelAndView modelAndView = new ModelAndView("posts");
        modelAndView.addObject("postPage", adminPostDTOPage);

        return modelAndView;
    }


    @GetMapping("/search")
    public String adminSearchList(@ModelAttribute PageableParam pageableParam, String  searchKeyword, Model model) {
        Pageable pageable = PageRequest.of(pageableParam.getPage() , pageableParam.getSize(), Sort.by(Sort.Direction.DESC, "createdAt"));

        Page<AdminPostDTO.Search> searchByPage = adminPostService.adminSearch(searchKeyword, pageable).map(adminPostMapper::toSearchListDTOForAdmin);
        model.addAttribute("search", searchByPage);
        return "searchResults";
    }

    @GetMapping("/{id}")
    @Operation(summary = "게시글 상세 조회", description = "게시글 상세를 조회합니다.")
    public String postDetails(@PathVariable("id") Long id, Model model) {
        Post postDetails = adminPostService.findAdminPostDetails(id);
        AdminPostDTO.Details adminPostDetailsDTO = adminPostMapper.toAdminDetailsDTO(postDetails);
        model.addAttribute("adminPostDetails", adminPostDetailsDTO);
        return "postDetails";
    }
}
