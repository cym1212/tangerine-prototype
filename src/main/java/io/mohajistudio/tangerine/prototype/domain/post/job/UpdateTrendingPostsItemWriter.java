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
import java.util.stream.Collectors;

@Slf4j
@Component
@RequiredArgsConstructor
public class UpdateTrendingPostsItemWriter implements ItemWriter<TrendingPost>, StepExecutionListener {
    private final TrendingPostRepository trendingPostRepository;
    private static final int TRENDING_POST_SIZE = 50;
    private final Set<TrendingPost> trendingPostSet = new HashSet<>();

    @Override
    public void write(Chunk<? extends TrendingPost> chunk) {
        trendingPostSet.addAll(chunk.getItems());

        Set<TrendingPost> topTrendingPosts = trendingPostSet.stream()
                .sorted(Comparator.comparingDouble(TrendingPost::getScore).reversed())
                .limit(TRENDING_POST_SIZE).collect(Collectors.toSet());

        trendingPostSet.clear();
        trendingPostSet.addAll(topTrendingPosts);
    }

    @Override
    public ExitStatus afterStep(@NonNull @NotNull StepExecution stepExecution) {
        trendingPostRepository.deleteAll();

        int i = 0;
        for (TrendingPost post : trendingPostSet) {
            post.setId((long) i++);
        }

        trendingPostRepository.saveAll(trendingPostSet);
        trendingPostSet.clear();
        return ExitStatus.COMPLETED;
    }
}
