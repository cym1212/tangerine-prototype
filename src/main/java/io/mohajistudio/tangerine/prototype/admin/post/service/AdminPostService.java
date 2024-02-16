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
        return adminPostRepository.findAllByOrderByCreatedAtDesc(pageable);
    }


    public Page<Post> adminSearch(String searchKeyword, Pageable pageable) {
        return adminPostRepository.findByTitleOrNicknameContaining(searchKeyword, pageable);
    }
}
