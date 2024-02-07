package io.mohajistudio.tangerine.prototype.admin.post.service;

import io.mohajistudio.tangerine.prototype.admin.post.repository.AdminPostRepository;
import io.mohajistudio.tangerine.prototype.domain.post.domain.Post;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class AdminPostService {

    private final AdminPostRepository adminPostRepository;

    public Page<Post> adminPostList(Pageable pageable) {
        log.info("service");
        return adminPostRepository.findAllByOrderByCreatedAtDesc(pageable);
    }


    public Page<Post> postSearchList (String searchKeyword, Pageable pageable) {
        return adminPostRepository.findByTitleContaining(searchKeyword, pageable);
    }
}
