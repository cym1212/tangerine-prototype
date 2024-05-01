package io.mohajistudio.tangerine.prototype.domain.post.service;

import io.mohajistudio.tangerine.prototype.domain.member.domain.Member;
import io.mohajistudio.tangerine.prototype.domain.placeblock.domain.PlaceBlock;
import io.mohajistudio.tangerine.prototype.domain.placeblock.repository.PlaceBlockRepository;
import io.mohajistudio.tangerine.prototype.domain.placeblock.service.PlaceBlockService;
import io.mohajistudio.tangerine.prototype.domain.placeblockimage.domain.PlaceBlockImage;
import io.mohajistudio.tangerine.prototype.domain.placeblockimage.service.PlaceBlockImageService;
import io.mohajistudio.tangerine.prototype.domain.post.domain.*;
import io.mohajistudio.tangerine.prototype.domain.post.repository.*;
import io.mohajistudio.tangerine.prototype.global.enums.ErrorCode;
import io.mohajistudio.tangerine.prototype.global.enums.PostStatus;
import io.mohajistudio.tangerine.prototype.global.error.exception.BusinessException;
import io.mohajistudio.tangerine.prototype.global.error.exception.UrlNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;

import static io.mohajistudio.tangerine.prototype.global.enums.ErrorCode.*;

@Service
@Transactional
@RequiredArgsConstructor
public class PostService {
    private final PostRepository postRepository;
    private final FavoritePostRepository favoritePostRepository;
    private final TextBlockRepository textBlockRepository;
    private final PlaceBlockImageRepository placeBlockImageRepository;
    private final ScrapPostRepository scrapPostRepository;
    private final PlaceBlockImageService placeBlockImageService;
    private final PlaceBlockService placeBlockService;
    private final PlaceBlockRepository placeBlockRepository;


    private static final int MIN_POSTS_INTERVAL_MINUTES = 10;
    private static final int MAX_POSTS_INTERVAL_HOURS = 24;

    public Post addPost(Post post, Long memberId) {
        checkPostInterval(memberId);
        post.validate();
        post.setMember(Member.builder().id(memberId).build());
        post.setStatus(PostStatus.PUBLISHED);
        post.setThumbnail(placeBlockImageService.copyImageToPermanent(post.getThumbnail()));

        postRepository.save(post);

        post.getTextBlocks().forEach(textBlock -> {
            textBlock.setPost(post);
            textBlockRepository.save(textBlock);
        });

        post.getPlaceBlocks().forEach(placeBlock -> placeBlockService.addPlaceBlock(placeBlock, post));

        return post;
    }

    public Page<Post> findPostListByPage(Pageable pageable) {
        return postRepository.findAll(pageable);
    }

    public Page<Post> findPostListByKeywordPage(Pageable pageable, String keyword) {
        return postRepository.findAllContainingKeyword(pageable, keyword);
    }

    public Post findPostDetails(Long id, Long memberId) {
        Optional<Post> findPost = postRepository.findByIdDetails(id);

        if (findPost.isEmpty()) throw new UrlNotFoundException();

        Post post = findPost.get();

        if (memberId != null) {
            Optional<FavoritePost> findFavoritePost = favoritePostRepository.findByMemberIdAndPostId(memberId, id);
            post.setIsFavorite(findFavoritePost.isPresent());
        }

        return post;
    }

    public void modifyPost(Post modifyPost, Long memberId) {
        Optional<Post> findPost = postRepository.findById(modifyPost.getId());

        if (findPost.isEmpty()) {
            throw new UrlNotFoundException();
        }

        Post post = findPost.get();

        if(!Objects.equals(post.getMember().getId(), memberId)) {
            throw new BusinessException(NO_PERMISSION);
        }

        modifyPost.validate();

        checkDeletedBlocksAndImages(modifyPost.getPlaceBlocks(), modifyPost.getTextBlocks(), post.getPlaceBlocks(), post.getTextBlocks());

        modifyPost.setThumbnail(placeBlockImageService.copyImageToPermanent(modifyPost.getThumbnail()));

        LocalDateTime modifiedAt = LocalDateTime.now();

        postRepository.update(post.getId(), modifiedAt, modifyPost.getTitle(), modifyPost.getVisitStartDate(), modifyPost.getVisitEndDate(), modifyPost.getPlaceBlockCnt(), modifyPost.getThumbnail());

        modifyPost.getTextBlocks().forEach(textBlock -> modifyTextBlock(textBlock, post));
        modifyPost.getPlaceBlocks().forEach(placeBlock -> placeBlockService.modifyPlaceBlock(placeBlock, post));
    }

    private void modifyTextBlock(TextBlock textBlock, Post post) {
        if (textBlock.getId() == null) {
            textBlock.setPost(post);
            textBlockRepository.save(textBlock);
        } else {
            Optional<TextBlock> findTextBlock = textBlockRepository.findById(textBlock.getId());
            if (findTextBlock.isEmpty()) {
                throw new BusinessException(ErrorCode.INVALID_INPUT_VALUE);
            }
            textBlockRepository.update(findTextBlock.get().getId(), textBlock.getContent(), textBlock.getOrderNumber());
        }
    }

    public void modifyFavoritePost(Long id, Long memberId) {
        Optional<Post> findPost = postRepository.findById(id);
        if (findPost.isEmpty()) throw new UrlNotFoundException();
        Post post = findPost.get();

        Optional<FavoritePost> findFavoritePost = favoritePostRepository.findByMemberIdAndPostId(memberId, id);
        if (findFavoritePost.isPresent()) {
            FavoritePost favoritePost = findFavoritePost.get();
            favoritePostRepository.deleteById(favoritePost.getId());
            postRepository.updateFavoriteCnt(post.getId(), post.getFavoriteCnt() - 1);
        } else {
            Member member = Member.builder().id(memberId).build();
            FavoritePost favoritePost = FavoritePost.builder().member(member).post(post).build();
            favoritePostRepository.save(favoritePost);
            postRepository.updateFavoriteCnt(post.getId(), post.getFavoriteCnt() + 1);
        }
    }

    public void deletePost(Long id, Long memberId) {
        Optional<Post> findPost = postRepository.findByIdDetails(id);
        if (findPost.isEmpty()) throw new UrlNotFoundException();
        Post post = findPost.get();

        if (!post.getMember().getId().equals(memberId)) {
            throw new BusinessException(ErrorCode.NO_PERMISSION);
        }

        LocalDateTime deletedAt = LocalDateTime.now();

        postRepository.delete(id, deletedAt, PostStatus.DELETED);

        post.getTextBlocks().forEach(textBlock -> textBlockRepository.delete(textBlock.getId(), deletedAt));
        post.getPlaceBlocks().forEach(placeBlock -> {
            placeBlockRepository.delete(placeBlock.getId(), deletedAt);
            placeBlock.getPlaceBlockImages().forEach(placeBlockImage ->
                    placeBlockImageRepository.delete(placeBlockImage.getId(), deletedAt)
            );
        });
    }

    private void checkDeletedBlocksAndImages(Set<PlaceBlock> modifyPlaceBlocks, Set<TextBlock> modifyTextBlocks, Set<PlaceBlock> placeBlocks, Set<TextBlock> textBlocks) {
        Set<Long> blockIds = new HashSet<>();
        Set<Long> modifyBlockIds = new HashSet<>();
        Set<Long> placeBlockImageIds = new HashSet<>();
        Set<Long> modifyPlaceBlockImageIds = new HashSet<>();
        LocalDateTime deletedAt = LocalDateTime.now();

        textBlocks.forEach(textBlock -> blockIds.add(textBlock.getId()));
        modifyTextBlocks.forEach(textBlock -> modifyBlockIds.add(textBlock.getId()));
        blockIds.forEach(blockId -> {
            if (!modifyBlockIds.contains(blockId)) textBlockRepository.delete(blockId, deletedAt);
        });

        blockIds.clear();
        modifyBlockIds.clear();

        placeBlocks.forEach(placeBlock -> {
            blockIds.add(placeBlock.getId());
            placeBlock.getPlaceBlockImages().forEach(placeBlockImage -> placeBlockImageIds.add(placeBlockImage.getId()));
        });

        modifyPlaceBlocks.forEach(placeBlock -> {
            modifyBlockIds.add(placeBlock.getId());
            placeBlock.getPlaceBlockImages().forEach(placeBlockImage -> modifyPlaceBlockImageIds.add(placeBlockImage.getId()));
        });

        blockIds.forEach(blockId -> {
            if (!modifyBlockIds.contains(blockId)) placeBlockRepository.delete(blockId, deletedAt);
        });

        placeBlockImageIds.forEach(placeBlockImageId -> {
            if (!modifyPlaceBlockImageIds.contains(placeBlockImageId))
                placeBlockImageRepository.delete(placeBlockImageId, deletedAt);
        });
    }

    private void checkPostInterval(Long memberId) {
        LocalDateTime maxPostsIntervalHours = LocalDateTime.now().minusHours(MAX_POSTS_INTERVAL_HOURS);
        List<Post> findPosts = postRepository.findAllByMemberIdAfter(memberId, maxPostsIntervalHours);
        if (findPosts.size() >= 3) {
            throw new BusinessException(MAX_POSTS_PER_DAY);
        }

        if (!findPosts.isEmpty()) {
            if (findPosts.get(0).getCreatedAt().plusMinutes(MIN_POSTS_INTERVAL_MINUTES).isAfter(LocalDateTime.now())) {
                throw new BusinessException(TOO_FREQUENT_POST);
            }
        }
    }

    public void modifyScrapPost(Long id, Long memberId) {
        Optional<Post> findPost = postRepository.findById(id);
        if (findPost.isEmpty()) throw new UrlNotFoundException();
        Post post = findPost.get();

        Optional<ScrapPost> findScrapPost = scrapPostRepository.findByMemberIdAndPostId(memberId, id);
        if (findScrapPost.isPresent()) {
            ScrapPost scrapPost = findScrapPost.get();
            scrapPostRepository.delete(scrapPost);
        } else {
            Member member = Member.builder().id(memberId).build();
            ScrapPost favoritePost = ScrapPost.builder().member(member).post(post).build();
            scrapPostRepository.save(favoritePost);
        }
    }

    @Transactional
    public void permanentDelete(Long memberId) {
        int pageSize = 10;
        int page = 0;

        Page<Post> postListByPage;
        do {
            postListByPage = postRepository.findByMemberIdForWithdrawal(memberId, PageRequest.of(page, pageSize));
            List<Post> postList = postListByPage.getContent();

            postList.parallelStream().forEach(
                    post -> {
                        post.getPlaceBlocks().forEach(
                                placeBlock -> {
                                    Set<PlaceBlockImage> placeBlockImages = placeBlock.getPlaceBlockImages();
                                    placeBlockImages.forEach(
                                            placeBlockImageService::permanentDelete
                                    );
                                    placeBlockRepository.delete(placeBlock);
                                }
                        );
                        textBlockRepository.deleteAll(post.getTextBlocks());
                        postRepository.delete(post);
                    }
            );

            page++;
        } while (postListByPage.hasNext());
    }

    public Post modifyPostStatus(Long postId, PostStatus postStatus) {
        Optional<Post> findPost = postRepository.findByIdWithMember(postId);

        if (findPost.isEmpty()) throw new UrlNotFoundException();

        Post post = findPost.get();

        if(post.getStatus().equals(postStatus)) {
            return null;
        }

        postRepository.updatePostStatus(postId, postStatus);

        return findPost.get();
    }

    @Transactional
    public void permanentDeleteFavoritePost(Long memberId) {
        int pageSize = 10;
        int page = 0;

        Page<FavoritePost> favoritePostListByPage;
        do {
            favoritePostListByPage = favoritePostRepository.findByMemberIdForWithdrawal(memberId, PageRequest.of(page, pageSize));
            List<FavoritePost> favoritePostList = favoritePostListByPage.getContent();

            favoritePostList.parallelStream().forEach(
                    favoritePost -> {
                        Optional<Post> findPost = postRepository.findById(favoritePost.getPost().getId());
                        if (findPost.isPresent()) {
                            Post post = findPost.get();
                            postRepository.updateFavoriteCnt(post.getId(), post.getFavoriteCnt() - 1);
                        }
                        favoritePostRepository.delete(favoritePost);
                    }
            );
            page++;
        } while (favoritePostListByPage.hasNext());
    }
}
