package io.mohajistudio.tangerine.prototype.global.config;

import io.mohajistudio.tangerine.prototype.domain.post.domain.Post;
import io.mohajistudio.tangerine.prototype.domain.post.domain.TrendingPost;
import io.mohajistudio.tangerine.prototype.domain.post.job.UpdateTrendingPostsItemWriter;
import jakarta.persistence.EntityManagerFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.database.JpaPagingItemReader;
import org.springframework.batch.item.database.builder.JpaPagingItemReaderBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

import java.time.Duration;
import java.time.LocalDateTime;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class BatchConfig {
    final EntityManagerFactory entityManagerFactory;
    final UpdateTrendingPostsItemWriter updateTrendingPostsItemWriter;
    private static final int CHUNK_SIZE = 50;
    private static final double GRAVITY = 1.8;

    @Bean
    public Job updateTrendingPostsJob(PlatformTransactionManager transactionManager, JobRepository jobRepository) {
        return new JobBuilder("updateTrendingPostsJob", jobRepository)
                .start(updateTrendingPostsStep(transactionManager, jobRepository))
                .build();
    }

    @Bean
    public Step updateTrendingPostsStep(PlatformTransactionManager transactionManager, JobRepository jobRepository) {
        return new StepBuilder("updateTrendingPostsStep", jobRepository)
                .<Post, TrendingPost>chunk(CHUNK_SIZE, transactionManager)
                .reader(updateTrendingPostsItemReader())
                .processor(updateTrendingPostsItemProcessor())
                .writer(updateTrendingPostsItemWriter)
                .build();
    }

    @Bean
    JpaPagingItemReader<Post> updateTrendingPostsItemReader() {
        return new JpaPagingItemReaderBuilder<Post>()
                .name("updateTrendingPostsItemReader")
                .entityManagerFactory(entityManagerFactory)
                .queryString("SELECT p FROM Post p WHERE p.deletedAt IS NULL")
                .pageSize(CHUNK_SIZE)
                .build();
    }

    @Bean
    public ItemProcessor<Post, TrendingPost> updateTrendingPostsItemProcessor() {
        return post -> {
            long timeDifference = Duration.between(post.getCreatedAt(), LocalDateTime.now()).toHours() + 2;
            double score = (post.getFavoriteCnt() - 1) / (timeDifference * GRAVITY);
            return TrendingPost.builder().post(post).score(score).build();
        };
    }
}
