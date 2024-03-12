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
        List<TrendingPost> sortedTrendingPostList = trendingPostList.stream().sorted(Comparator.comparingDouble(TrendingPost::getScore)).toList();
        sortedTrendingPostList.subList(0, Math.min(trendingPostList.size(), TRENDING_POST_SIZE));
        trendingPostList.clear();
        trendingPostList.addAll(sortedTrendingPostList);
    }

    @Override
    public ExitStatus afterStep(@NonNull @NotNull StepExecution stepExecution) {
        trendingPostRepository.deleteAll();
        trendingPostRepository.saveAll(trendingPostList);
        return ExitStatus.COMPLETED;
    }
}
