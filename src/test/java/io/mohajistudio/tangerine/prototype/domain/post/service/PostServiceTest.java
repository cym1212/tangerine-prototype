package io.mohajistudio.tangerine.prototype.domain.post.service;

import io.mohajistudio.tangerine.prototype.domain.place.domain.PlaceCategory;
import io.mohajistudio.tangerine.prototype.domain.member.domain.Member;
import io.mohajistudio.tangerine.prototype.domain.member.repository.MemberRepository;
import io.mohajistudio.tangerine.prototype.domain.place.domain.Place;
import io.mohajistudio.tangerine.prototype.domain.post.domain.*;
import io.mohajistudio.tangerine.prototype.domain.post.repository.*;
import io.mohajistudio.tangerine.prototype.global.common.BaseEntity;
import io.mohajistudio.tangerine.prototype.global.enums.ImageMimeType;
import io.mohajistudio.tangerine.prototype.global.error.exception.BusinessException;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDate;
import java.util.*;

@SpringBootTest
@Slf4j
@Transactional
@ActiveProfiles("dev")
class PostServiceTest {
    @Autowired
    private PostService postService;
    @Autowired
    private PostRepository postRepository;
    @Autowired
    private MemberRepository memberRepository;

    Post createPost() {
        Set<TextBlock> textBlocks = new HashSet<>();
        Set<PlaceBlock> placeBlocks = new HashSet<>();
        Set<PlaceBlockImage> placeBlockImages = new HashSet<>();

        Post post = Post.builder().title("테스트 타이틀").visitStartDate(LocalDate.of(2023, 12, 25)).visitEndDate(LocalDate.of(2023, 12, 25)).build();
        PlaceCategory category = PlaceCategory.builder().id(1L).build();
        Place place = Place.builder().id(1L).build();

        textBlocks.add(TextBlock.builder().content("첫 번째 블럭에 들어갈 내용입니다.").orderNumber((short) 1).build());
        textBlocks.add(TextBlock.builder().content("세 번째 블럭에 들어갈 내용입니다.").orderNumber((short) 3).build());

        placeBlockImages.add(PlaceBlockImage.builder().imageUrl("https://image-url-2-1.com").imageMimeType(ImageMimeType.JPEG).orderNumber((short) 1).build());
        placeBlockImages.add(PlaceBlockImage.builder().imageUrl("https://image-url-2-2.com").imageMimeType(ImageMimeType.PNG).orderNumber((short) 1).build());

        placeBlocks.add(PlaceBlock.builder().content("두 번째 블럭에 들어갈 내용입니다.").orderNumber((short) 2).placeCategory(category).place(place).placeBlockImages(placeBlockImages).build());

        post.setTextBlocks(textBlocks);
        post.setPlaceBlocks(placeBlocks);
        return post;
    }

    Post createModifyPost(Post post) {
        Set<TextBlock> textBlocks = new HashSet<>();
        Set<PlaceBlock> placeBlocks = new HashSet<>();
        Set<PlaceBlockImage> placeBlockImages = new HashSet<>();

        Post modifyPost = Post.builder().title("수정된 테스트 타이틀").visitStartDate(LocalDate.of(2023, 12, 25)).visitEndDate(LocalDate.of(2023, 12, 25)).id(post.getId()).build();
        PlaceCategory category = PlaceCategory.builder().id(1L).build();
        Place place = Place.builder().id(1L).build();

        List<Long> textBlockIds = post.getTextBlocks().stream().map(BaseEntity::getId).toList();
        textBlocks.add(TextBlock.builder().content("세 번째였던 첫 번째 블럭에 들어갈 내용입니다.").orderNumber((short) 1).id(textBlockIds.get(1)).build());

        List<Long> placeBlockIds = new ArrayList<>();
        List<Long> placeBlockImageIds = new ArrayList<>();

        post.getPlaceBlocks().forEach(placeBlock -> {
            placeBlockIds.add(placeBlock.getId());
            placeBlock.getPlaceBlockImages().forEach(placeBlockImage -> placeBlockImageIds.add(placeBlockImage.getId()));
        });

        placeBlockImages.add(PlaceBlockImage.builder().imageUrl("https://image-url-2-1.com").imageMimeType(ImageMimeType.JPEG).orderNumber((short) 1).id(placeBlockImageIds.get(0)).build());
        placeBlockImages.add(PlaceBlockImage.builder().imageUrl("https://image-url-2-2.com").imageMimeType(ImageMimeType.PNG).orderNumber((short) 1).id(placeBlockImageIds.get(1)).build());

        Set<PlaceBlockImage> placeBlockImages2 = new HashSet<>();
        placeBlockImages2.add(PlaceBlockImage.builder().imageUrl("https://image-url-3-1.com").imageMimeType(ImageMimeType.JPEG).orderNumber((short) 1).build());
        placeBlockImages2.add(PlaceBlockImage.builder().imageUrl("https://image-url-3-2.com").imageMimeType(ImageMimeType.PNG).orderNumber((short) 1).build());

 /*       placeBlocks.add(PlaceBlock.builder().content("수정된 두 번째 블럭에 들어갈 내용입니다.").orderNumber((short) 2).category(category).place(place).placeBlockImages(placeBlockImages).id(placeBlockIds.get(0)).build());
        placeBlocks.add(PlaceBlock.builder().content("추가된 세 번째 블럭에 들어갈 내용입니다.").orderNumber((short) 3).category(category).place(place).placeBlockImages(placeBlockImages2).build());
*/
        modifyPost.setTextBlocks(textBlocks);
        modifyPost.setPlaceBlocks(placeBlocks);
        return modifyPost;
    }

    @Test
    void addPost() {
        Optional<Member> findMember = memberRepository.findById(1L);

        Post post = createPost();

        postService.addPost(post, findMember.get().getId());

        Optional<Post> findPost = postRepository.findByIdDetails(post.getId());

        Assertions.assertTrue(findPost.isPresent());
    }

    @Test
    void findPostListByPage() {
        int times = 4;
        Optional<Member> findMember = memberRepository.findById(1L);
        Assertions.assertTrue(findMember.isPresent());

        PageRequest pageRequest = PageRequest.of(0, 10, Sort.by("id").ascending());
        for (int i = 0; i < times; i++) {
            Post post = createPost();
            post.setMember(findMember.get());
            postRepository.save(post);
        }

        Page<Post> postListByPage = postService.findPostListByPage(pageRequest);

        Assertions.assertEquals(postListByPage.stream().count(), times);
    }

    @Test
    void findPostDetails() {

    }

    @Test
    void modifyPost() {
        Optional<Member> findMember = memberRepository.findById(1L);
        Member member = findMember.get();
        Post post = createPost();
        post.setMember(member);
        postService.addPost(post, member.getId());

        Post modifyPost = createModifyPost(post);
        postService.modifyPost(modifyPost, member.getId());
        Assertions.assertEquals(modifyPost.getId(), post.getId());

        Optional<Post> findModifiedPost = postRepository.findByIdDetails(modifyPost.getId());
        Assertions.assertTrue(findModifiedPost.isPresent());

        Post modifiedPost = findModifiedPost.get();
        Assertions.assertEquals(1, modifiedPost.getTextBlocks().size());
        Assertions.assertEquals(2, modifiedPost.getPlaceBlocks().size());
        modifiedPost.getPlaceBlocks().forEach(placeBlock -> Assertions.assertEquals(2, placeBlock.getPlaceBlockImages().size()));
    }

    @Test
    void modifyFavoritePost() {
    }

    @Test
    void deletePost() {
    }

    @Test
    void countPostsToday() {
        Optional<Member> findMember = memberRepository.findById(1L);
        Member member = findMember.get();
        Post post = createPost();
        post.setMember(member);
        postService.addPost(post, member.getId());
        Assertions.assertThrows(BusinessException.class, () -> postService.addPost(post, member.getId()));
    }
}