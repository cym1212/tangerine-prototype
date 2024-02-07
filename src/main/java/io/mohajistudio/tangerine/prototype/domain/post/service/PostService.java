package io.mohajistudio.tangerine.prototype.domain.post.service;

import io.mohajistudio.tangerine.prototype.domain.member.domain.Member;
import io.mohajistudio.tangerine.prototype.domain.member.repository.MemberRepository;
import io.mohajistudio.tangerine.prototype.domain.placeblockimage.domain.PlaceBlockImage;
import io.mohajistudio.tangerine.prototype.domain.placeblockimage.service.PlaceBlockImageService;
import io.mohajistudio.tangerine.prototype.domain.post.domain.*;
import io.mohajistudio.tangerine.prototype.domain.post.repository.*;
import io.mohajistudio.tangerine.prototype.global.enums.ErrorCode;
import io.mohajistudio.tangerine.prototype.global.error.exception.BusinessException;
import io.mohajistudio.tangerine.prototype.global.error.exception.UrlNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;

import static io.mohajistudio.tangerine.prototype.global.enums.ErrorCode.*;

@Service
@RequiredArgsConstructor
public class PostService{
    private final PostRepository postRepository;
    private final MemberRepository memberRepository;
    private final FavoritePostRepository favoritePostRepository;
    private final TextBlockRepository textBlockRepository;
    private final PlaceBlockRepository placeBlockRepository;
    private final PlaceBlockImageRepository placeBlockImageRepository;
    private final ScrapPostRepository scrapPostRepository;
    private final PlaceBlockImageService placeBlockImageService;

    private static final int MIN_POSTS_INTERVAL_MINUTES = 10;
    private static final int MAX_POSTS_INTERVAL_HOURS = 24;

    @Transactional
    public void addPost(Post post, Long memberId) {
        countPostsToday(memberId);
        checkBlockOrderNumberAndContentIsEmpty(post.getPlaceBlocks(), post.getTextBlocks());

        Optional<Member> findMember = memberRepository.findById(memberId);
        findMember.ifPresent(post::setMember);

        postRepository.save(post);

        post.getTextBlocks().forEach(textBlock -> {
            textBlock.setPost(post);
            textBlockRepository.save(textBlock);
        });

        post.getPlaceBlocks().forEach(placeBlock -> {
            placeBlock.setPost(post);
            placeBlockRepository.save(placeBlock);
            placeBlock.getPlaceBlockImages().forEach(placeBlockImage -> {
                placeBlockImage.setPlaceBlock(placeBlock);
                placeBlockImageRepository.save(placeBlockImage);
                if (placeBlock.getRepresentativePlaceBlockImageOrderNumber() == placeBlockImage.getOrderNumber()) {
                    placeBlock.setRepresentativePlaceBlockImageId(placeBlockImage.getId());
                    placeBlockRepository.update(placeBlock.getId(), placeBlockImage.getId());
                }
            });
            if (placeBlock.getRepresentativePlaceBlockImageId() == null) {
                throw new BusinessException(INVALID_REPRESENTATIVE_PLACE_BLOCK_IMAGE_ORDER_NUMBER);
            }
            placeBlockImageService.copyImagesToPermanent(placeBlock.getPlaceBlockImages());
        });
    }

    public Page<Post> findPostListByPage(Pageable pageable) {
        return postRepository.findAll(pageable);
    }

    public Post findPostDetails(Long id) {
        Optional<Post> findPost = postRepository.findByIdDetails(id);
        if (findPost.isEmpty()) throw new UrlNotFoundException();
        return findPost.get();
    }

    @Transactional
    public void modifyPost(Post modifyPost, Long memberId) {
        Optional<Post> findPost = postRepository.findById(modifyPost.getId());

        if (findPost.isEmpty()) {
            throw new UrlNotFoundException();
        }

        Post post = findPost.get();

        if (!post.getMember().getId().equals(memberId)) {
            throw new BusinessException(ErrorCode.NO_PERMISSION);
        }

        checkBlockOrderNumberAndContentIsEmpty(modifyPost.getPlaceBlocks(), modifyPost.getTextBlocks());
        checkDeletedBlock(modifyPost.getPlaceBlocks(), modifyPost.getTextBlocks(), post.getPlaceBlocks(), post.getTextBlocks());

        postRepository.update(post.getId(), modifyPost.getTitle(), modifyPost.getVisitStartDate(), modifyPost.getVisitEndDate());

        modifyPost.getTextBlocks().forEach(textBlock -> modifyTextBlock(textBlock, post));
        modifyPost.getPlaceBlocks().forEach(placeBlock -> {
            modifyPlaceBlock(placeBlock, post);
            placeBlock.getPlaceBlockImages().forEach(placeBlockImage -> {
                        modifyPlaceBlockImage(placeBlock, placeBlockImage);
                        if (placeBlock.getRepresentativePlaceBlockImageId() == null && placeBlock.getRepresentativePlaceBlockImageOrderNumber() == placeBlockImage.getOrderNumber()) {
                            placeBlock.setRepresentativePlaceBlockImageId(placeBlockImage.getId());
                        }
                    }
            );
            if (placeBlock.getRepresentativePlaceBlockImageId() == null) {
                throw new BusinessException(INVALID_REPRESENTATIVE_PLACE_BLOCK_IMAGE_ORDER_NUMBER);
            }
            placeBlockImageService.copyImagesToPermanent(placeBlock.getPlaceBlockImages());
        });
    }

    private void modifyPlaceBlock(PlaceBlock placeBlock, Post post) {
        if (placeBlock.getId() == null) {
            placeBlock.setPost(post);
            placeBlockRepository.save(placeBlock);
        } else {
            Optional<PlaceBlock> findPlaceBlock = placeBlockRepository.findById(placeBlock.getId());
            if (findPlaceBlock.isEmpty()) {
                throw new BusinessException(ErrorCode.INVALID_INPUT_VALUE);
            }
            placeBlockRepository.update(findPlaceBlock.get().getId(), placeBlock.getContent(), placeBlock.getOrderNumber(), placeBlock.getRating(), placeBlock.getPlaceCategory(), placeBlock.getPlace());
        }
    }

    private void modifyPlaceBlockImage(PlaceBlock placeBlock, PlaceBlockImage placeBlockImage) {
        if (placeBlockImage.getId() == null) {
            placeBlockImage.setPlaceBlock(placeBlock);
            placeBlockImageRepository.save(placeBlockImage);
        } else {
            Optional<PlaceBlockImage> findPlaceBlockImage = placeBlockImageRepository.findById(placeBlockImage.getId());
            if (findPlaceBlockImage.isEmpty()) {
                throw new BusinessException(ErrorCode.INVALID_INPUT_VALUE);
            }
            placeBlockImageRepository.update(findPlaceBlockImage.get().getId(), placeBlockImage.getImageUrl(), placeBlockImage.getImageMimeType(), placeBlockImage.getOrderNumber());
        }
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

    @Transactional
    public void modifyFavoritePost(Long id, Long memberId) {
        Optional<Post> findPost = postRepository.findById(id);
        if (findPost.isEmpty()) throw new UrlNotFoundException();
        Post post = findPost.get();

        Optional<FavoritePost> findFavoritePost = favoritePostRepository.findByMemberIdAndPostId(memberId, id);
        if (findFavoritePost.isPresent()) {
            FavoritePost favoritePost = findFavoritePost.get();
            favoritePostRepository.delete(favoritePost);
            postRepository.updateFavoriteCnt(post.getId(), post.getFavoriteCnt() - 1);
        } else {
            Member member = Member.builder().id(memberId).build();
            FavoritePost favoritePost = FavoritePost.builder().member(member).post(post).build();
            favoritePostRepository.save(favoritePost);
            postRepository.updateFavoriteCnt(post.getId(), post.getFavoriteCnt() + 1);
        }
    }

    @Transactional
    public void deletePost(Long id, Long memberId) {
        Optional<Post> findPost = postRepository.findByIdDetails(id);
        if (findPost.isEmpty()) throw new UrlNotFoundException();
        Post post = findPost.get();

        if (!post.getMember().getId().equals(memberId)) {
            throw new BusinessException(ErrorCode.NO_PERMISSION);
        }

        LocalDateTime deletedAt = LocalDateTime.now();

        postRepository.delete(id, deletedAt);

        post.getTextBlocks().forEach(textBlock -> textBlockRepository.delete(textBlock.getId(), deletedAt));
        post.getPlaceBlocks().forEach(placeBlock -> {
            placeBlockRepository.delete(placeBlock.getId(), deletedAt);
            placeBlock.getPlaceBlockImages().forEach(placeBlockImage ->
                    placeBlockImageRepository.delete(placeBlockImage.getId(), deletedAt)
            );
        });
    }

    private void checkBlockOrderNumberAndContentIsEmpty(Set<PlaceBlock> placeBlocks, Set<TextBlock> textBlocks) {
        Set<Short> orderNumbers = new HashSet<>();

        placeBlocks.forEach(placeBlock -> {
            if (!orderNumbers.add(placeBlock.getOrderNumber())) {
                throw new BusinessException(ErrorCode.INVALID_ORDER_NUMBER);
            }
            if (placeBlock.getContent().isEmpty()) {
                throw new BusinessException(ErrorCode.CONTENT_IS_EMPTY);
            }
        });
        textBlocks.forEach(textBlock -> {
            if (!orderNumbers.add(textBlock.getOrderNumber())) {
                throw new BusinessException(ErrorCode.INVALID_ORDER_NUMBER);
            }
            if (textBlock.getContent().isEmpty()) {
                throw new BusinessException(ErrorCode.CONTENT_IS_EMPTY);
            }
        });

        int totalSize = placeBlocks.size() + textBlocks.size();
        for (short i = 1; i <= totalSize; i++) {
            if (!orderNumbers.contains(i)) {
                throw new BusinessException(ErrorCode.INVALID_ORDER_NUMBER);
            }
        }
    }

    private void checkDeletedBlock(Set<PlaceBlock> modifyPlaceBlocks, Set<TextBlock> modifyTextBlocks, Set<PlaceBlock> placeBlocks, Set<TextBlock> textBlocks) {
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

    private void countPostsToday(Long memberId) {
        LocalDateTime twentyFourHoursAgo = LocalDateTime.now().minusHours(MAX_POSTS_INTERVAL_HOURS);
        List<Post> findPosts = postRepository.countPostsToday(memberId, twentyFourHoursAgo);
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
}
