package io.mohajistudio.tangerine.prototype.domain.post.job;

import io.mohajistudio.tangerine.prototype.domain.post.domain.TrendingPost;
import io.mohajistudio.tangerine.prototype.domain.post.repository.TrendingPostRepository;
import jakarta.validation.constraints.NotNull;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.StepExecutionListener;
import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemWriter;
import org.springframework.stereotype.Component;

import java.util.*;

@Slf4j
@Component
@RequiredArgsConstructor
public class UpdateTrendingPostsItemWriter implements ItemWriter<TrendingPost>, StepExecutionListener {
    private final TrendingPostRepository trendingPostRepository;
    private static final int TRENDING_POST_SIZE = 50;
    private final List<TrendingPost> trendingPostList = new ArrayList<>();

    @Override
    public void write(Chunk<? extends TrendingPost> chunk) {
        trendingPostList.addAll(chunk.getItems());
        trendingPostList.sort(Comparator.comparingDouble(TrendingPost::getScore).reversed());
        if (trendingPostList.size() > TRENDING_POST_SIZE) {
            trendingPostList.subList(0, TRENDING_POST_SIZE).clear();
        }
    }

    @Override
    public ExitStatus afterStep(@NonNull @NotNull StepExecution stepExecution) {
        trendingPostRepository.deleteAll();
        List<TrendingPost> all = trendingPostRepository.findAll();
        log.info("size = " + all.size());
        for (int i = 0; i < trendingPostList.size(); i++) {
            trendingPostList.get(i).setId((long) i);
            log.info("postId = " + trendingPostList.get(i).getPost().getId() + ", score = " + trendingPostList.get(i).getScore());
        }
        trendingPostRepository.saveAll(trendingPostList);
        trendingPostList.clear();
        return ExitStatus.COMPLETED;
    }
}
