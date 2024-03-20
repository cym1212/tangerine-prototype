package io.mohajistudio.tangerine.prototype.domain.post.service;

import io.mohajistudio.tangerine.prototype.domain.post.domain.TrendingPost;
import io.mohajistudio.tangerine.prototype.domain.post.repository.TrendingPostRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@Transactional
@RequiredArgsConstructor
public class TrendingPostService {
    private final TrendingPostRepository trendingPostRepository;

    public Page<TrendingPost> findTrendingPostListByPage(Pageable pageable) {
        return trendingPostRepository.findAll(pageable);
    }
}
