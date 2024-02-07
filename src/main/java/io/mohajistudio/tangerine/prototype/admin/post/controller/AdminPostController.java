package io.mohajistudio.tangerine.prototype.admin.post.controller;


import io.mohajistudio.tangerine.prototype.admin.post.dto.AdminPostDTO;
import io.mohajistudio.tangerine.prototype.admin.post.mapper.AdminPostMapper;
import io.mohajistudio.tangerine.prototype.admin.post.service.AdminPostService;
import io.mohajistudio.tangerine.prototype.global.common.PageableParam;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
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
        Pageable pageable = PageRequest.of(pageableParam.getPage() - 1, pageableParam.getSize(), Sort.by(Sort.Direction.DESC, "createdAt"));

        Page<AdminPostDTO.Compact> adminPostDTOPage = adminPostService.adminPostList(pageable)
                .map(adminPostMapper::toCompactDTOForAdmin);

        ModelAndView modelAndView = new ModelAndView("posts");
        modelAndView.addObject("postPage", adminPostDTOPage);
        log.info("test");

        return modelAndView;
    }

//todo
    // 여기서 부터 다시 해야되고 Service 코드 연결 할 컨트롤러 코드 작성, () 안에 들어갈 값들이랑 DTO 코드 작성후 Mapper 코드 작성해야함
    @GetMapping("/searchlist")
    public String  postSearchList() {

        return "redirect:posts";
    }
}
