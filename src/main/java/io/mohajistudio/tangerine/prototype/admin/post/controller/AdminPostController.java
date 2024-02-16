package io.mohajistudio.tangerine.prototype.admin.post.controller;


import io.mohajistudio.tangerine.prototype.admin.post.dto.AdminPostDTO;
import io.mohajistudio.tangerine.prototype.admin.post.mapper.AdminPostMapper;
import io.mohajistudio.tangerine.prototype.admin.post.service.AdminPostService;
import io.mohajistudio.tangerine.prototype.domain.post.domain.Post;
import io.mohajistudio.tangerine.prototype.global.common.PageableParam;
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
        Pageable pageable = PageRequest.of(pageableParam.getPage() - 1, pageableParam.getSize(), Sort.by(Sort.Direction.DESC, "createdAt"));

        Page<AdminPostDTO.Compact> adminPostDTOPage = adminPostService.adminPostList(pageable)
                .map(adminPostMapper::toCompactDTOForAdmin);

        ModelAndView modelAndView = new ModelAndView("posts");
        modelAndView.addObject("postPage", adminPostDTOPage);

        return modelAndView;
    }

//todo
    //searchList 에  breakpoint 잡고 검색 결과 넘어오는지 확인 (안넘어오면 코드 수정)(repository코드에서 검색하는 부분이 문제인듯)  , 로직 테스트 , 서비스 로직 짜기

    @GetMapping("searchlist")
    public String adminSearchList(@ModelAttribute PageableParam pageableParam, String  searchKeyword, Model model) {
        Pageable pageable = PageRequest.of(pageableParam.getPage() - 1, pageableParam.getSize(), Sort.by(Sort.Direction.DESC, "createdAt"));

        Page<Post> searchList = adminPostService.adminSearch(searchKeyword, pageable);

//        List<Post> postList = searchList.getContent();
//        List<AdminSearchListDTO> searchListDTOList = new ArrayList<>();
//
//        for (Post post : postList) {
//            AdminSearchListDTO searchListDTO = adminPostMapper.toSearchListDTOForAdmin(post);
//            searchListDTOList.add(searchListDTO);
//        }
//
//        model.addAttribute("searchListDTOForAdmin", searchListDTOList);

        model.addAttribute("searchList", searchList);

        System.out.println("searchList = " + searchList);
        return "searchResults";
    }
}
