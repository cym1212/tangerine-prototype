package io.mohajistudio.tangerine.prototype.domain.post.service;

import io.mohajistudio.tangerine.prototype.domain.member.domain.Member;
import io.mohajistudio.tangerine.prototype.domain.member.repository.MemberRepository;
import io.mohajistudio.tangerine.prototype.domain.post.domain.*;
import io.mohajistudio.tangerine.prototype.domain.post.repository.*;
import io.mohajistudio.tangerine.prototype.global.enums.ErrorCode;
import io.mohajistudio.tangerine.prototype.global.error.exception.BusinessException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PostService {
    private final PostRepository postRepository;
    private final MemberRepository memberRepository;
    private final FavoritePostRepository favoritePostRepository;
    private final TextBlockRepository textBlockRepository;
    private final PlaceBlockRepository placeBlockRepository;
    private final PlaceBlockImageRepository placeBlockImageRepository;

    public Post addPost(Post post, Long memberId) {
        Optional<Member> findMember = memberRepository.findById(memberId);
        findMember.ifPresent(post::setMember);

        post.getTextBlocks().forEach(textBlock -> textBlock.setPost(post));

        post.getPlaceBlocks().forEach(placeBlock -> {
            placeBlock.setPost(post);
            placeBlock.getPlaceBlockImages().forEach(placeBlockImage -> placeBlockImage.setPlaceBlock(placeBlock));
        });

        return postRepository.save(post);
    }

    public Page<Post> findPostListByPage(Pageable pageable) {
        return postRepository.findAll(pageable);
    }

    public Post findPostDetails(Long id) {
        Optional<Post> findPost = postRepository.findById(id);
        if (findPost.isEmpty()) throw new BusinessException(ErrorCode.URL_NOT_FOUND);
        return findPost.get();
    }

    @Transactional
    public void modifyPost(Long memberId, Post modifyPost) {
        Optional<Post> findPost = postRepository.findById(modifyPost.getId());

        if (findPost.isEmpty()) {
            throw new BusinessException(ErrorCode.URL_NOT_FOUND);
        }

        Post post = findPost.get();

        if (!post.getMember().getId().equals(memberId)) {
            throw new BusinessException(ErrorCode.NO_PERMISSION);
        }

        post.getTextBlocks().forEach(textBlock -> modifyTextBlock(textBlock, post));
        post.getPlaceBlocks().forEach(placeBlock -> {
            modifyPlaceBlock(placeBlock, post);
            placeBlock.getPlaceBlockImages().forEach(placeBlockImage ->
                    modifyPlaceBlockImage(placeBlock, placeBlockImage)
            );
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
            placeBlockRepository.update(findPlaceBlock.get().getId(), placeBlock.getContent(), placeBlock.getOrderNumber(), placeBlock.getRating());
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
            placeBlockImageRepository.update(findPlaceBlockImage.get().getId(), placeBlockImage.getImageUrl(), placeBlockImage.getImageMimeType().name(), placeBlockImage.getOrderNumber());
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
        if (findPost.isEmpty()) throw new BusinessException(ErrorCode.URL_NOT_FOUND);
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
}
