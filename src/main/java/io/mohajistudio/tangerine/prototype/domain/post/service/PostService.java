package io.mohajistudio.tangerine.prototype.domain.post.service;

import io.mohajistudio.tangerine.prototype.domain.member.domain.Member;
import io.mohajistudio.tangerine.prototype.domain.member.repository.MemberRepository;
import io.mohajistudio.tangerine.prototype.domain.place.domain.Place;
import io.mohajistudio.tangerine.prototype.domain.place.repository.PlaceRepository;
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
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;

import static io.mohajistudio.tangerine.prototype.global.enums.ErrorCode.*;

@Service
@Transactional
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
    private final PlaceRepository placeRepository;
    private static final int MIN_POSTS_INTERVAL_MINUTES = 10;
    private static final int MAX_POSTS_INTERVAL_HOURS = 24;

    public void addPost(Post post, Long memberId) {
        checkPostInterval(memberId);

        post.validate();

        Optional<Member> findMember = memberRepository.findById(memberId);

        findMember.ifPresent(post::setMember);

        post.setStatus(PostStatus.PUBLISHED);

        String thumbnail = placeBlockImageService.copyImageToPermanent(post.getThumbnail());

        post.setThumbnail(thumbnail);

        postRepository.save(post);

        post.getTextBlocks().forEach(textBlock -> {
            textBlock.setPost(post);
            textBlockRepository.save(textBlock);
        });

        post.getPlaceBlocks().forEach(placeBlock -> {
            placeBlock.setPost(post);
            placeBlock.setMember(post.getMember());

            Place place = placeBlock.getPlace();
            Optional<Place> findPlace = placeRepository.findByProviderId(place.getProviderId());
            if (findPlace.isEmpty()) {
                throw new BusinessException(ENTITY_NOT_FOUND);
            }
            placeBlock.setPlace(findPlace.get());
            placeBlockImageService.copyImagesToPermanent(placeBlock.getPlaceBlockImages());

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
        });
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
            Optional<FavoritePost> findFavoritePost = favoritePostRepository.findByMemberIdAndPostId(id, memberId);
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

        if (!post.getMember().getId().equals(memberId)) {
            throw new BusinessException(ErrorCode.NO_PERMISSION);
        }

        modifyPost.validate();

        checkDeletedBlocksAndImages(modifyPost.getPlaceBlocks(), modifyPost.getTextBlocks(), post.getPlaceBlocks(), post.getTextBlocks());

        modifyPost.setThumbnail(placeBlockImageService.copyImageToPermanent(modifyPost.getThumbnail()));

        LocalDateTime modifiedAt = LocalDateTime.now();

        postRepository.update(post.getId(), modifiedAt, modifyPost.getTitle(), modifyPost.getVisitStartDate(), modifyPost.getVisitEndDate(), modifyPost.getPlaceBlockCnt(), modifyPost.getThumbnail());

        modifyPost.getTextBlocks().forEach(textBlock -> modifyTextBlock(textBlock, post));
        modifyPost.getPlaceBlocks().forEach(placeBlock -> {
            modifyPlaceBlock(placeBlock, post);
            placeBlock.getPlaceBlockImages().forEach(placeBlockImage -> {
                        modifyPlaceBlockImage(placeBlock, placeBlockImage);
                        if (placeBlock.getRepresentativePlaceBlockImageOrderNumber() == placeBlockImage.getOrderNumber()) {
                            placeBlock.setRepresentativePlaceBlockImageId(placeBlockImage.getId());
                            placeBlockRepository.update(placeBlock.getId(), placeBlockImage.getId());
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
        if (placeBlock.getPlace().getId() == null) {
            Place place = placeBlock.getPlace();
            Optional<Place> findPlace = placeRepository.findByProviderId(place.getProviderId());
            if (findPlace.isEmpty()) {
                throw new BusinessException(ENTITY_NOT_FOUND);
            }
            placeBlock.setPlace(findPlace.get());
        }

        if (placeBlock.getId() == null) {
            placeBlock.setPost(post);
            placeBlock.setMember(post.getMember());
            placeBlockRepository.save(placeBlock);
        } else {
            Optional<PlaceBlock> findPlaceBlock = placeBlockRepository.findById(placeBlock.getId());
            if (findPlaceBlock.isEmpty()) {
                throw new BusinessException(ErrorCode.INVALID_INPUT_VALUE);
            }
            placeBlockRepository.update(findPlaceBlock.get().getId(), placeBlock.getContent(), placeBlock.getOrderNumber(), placeBlock.getRating(), placeBlock.getPlaceCategory(), placeBlock.getPlace(), placeBlock.getVisitStartDate(), placeBlock.getVisitEndDate());
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
            placeBlockImageRepository.update(findPlaceBlockImage.get().getId(), placeBlockImage.getStorageKey(), placeBlockImage.getOrderNumber());
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
}
