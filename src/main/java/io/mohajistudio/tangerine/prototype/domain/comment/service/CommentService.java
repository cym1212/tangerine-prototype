package io.mohajistudio.tangerine.prototype.domain.comment.service;

import io.mohajistudio.tangerine.prototype.domain.comment.domain.Comment;
import io.mohajistudio.tangerine.prototype.domain.comment.domain.FavoriteComment;
import io.mohajistudio.tangerine.prototype.domain.comment.repository.CommentRepository;
import io.mohajistudio.tangerine.prototype.domain.comment.repository.FavoriteCommentRepository;
import io.mohajistudio.tangerine.prototype.domain.member.domain.Member;
import io.mohajistudio.tangerine.prototype.domain.member.repository.MemberRepository;
import io.mohajistudio.tangerine.prototype.domain.notification.service.NotificationService;
import io.mohajistudio.tangerine.prototype.domain.post.domain.Post;
import io.mohajistudio.tangerine.prototype.domain.post.repository.PostRepository;
import io.mohajistudio.tangerine.prototype.global.enums.CommentStatus;
import io.mohajistudio.tangerine.prototype.global.enums.ErrorCode;
import io.mohajistudio.tangerine.prototype.global.error.exception.BusinessException;
import io.mohajistudio.tangerine.prototype.global.error.exception.UrlNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

@Service
@RequiredArgsConstructor
@Transactional
public class CommentService {
    private final CommentRepository commentRepository;
    private final MemberRepository memberRepository;
    private final PostRepository postRepository;
    private final FavoriteCommentRepository favoriteCommentRepository;
    private final NotificationService notificationService;

    public Comment AddComment(Comment comment, Long postId, Long memberId) {
        Optional<Member> findMember = memberRepository.findById(memberId);

        if (findMember.isEmpty()) {
            throw new BusinessException(ErrorCode.NO_PERMISSION);
        }
        Member member = findMember.get();
        comment.setMember(member);

        Optional<Post> findPost = postRepository.findByIdWithMember(postId);
        if (findPost.isEmpty()) {
            throw new UrlNotFoundException();
        }
        Post post = findPost.get();

        comment.setPost(post);

        // 부모 댓글과 대댓글 대상이 없는 경우
        if (comment.getReplyComment() == null && comment.getParentComment() == null) {
            Integer maxGroupNumber = commentRepository.findMaxGroupNumberByPostId(postId);
            if (maxGroupNumber == null) {
                comment.setGroupNumber(0);
            } else {
                comment.setGroupNumber(maxGroupNumber + 1);
            }
        } else if (comment.getReplyComment() != null && comment.getParentComment() != null) {
            Optional<Comment> findParentComment = commentRepository.findByIdWithMember(comment.getParentComment().getId());
            Optional<Comment> findReplyComment = commentRepository.findByIdWithMember(comment.getReplyComment().getId());

            if (findParentComment.isEmpty() || findReplyComment.isEmpty()) {
                throw new BusinessException(ErrorCode.INVALID_INPUT_VALUE);
            }
            Comment parentComment = findParentComment.get();
            Comment replyComment = findReplyComment.get();

            comment.setGroupNumber(parentComment.getGroupNumber());
            comment.setParentComment(parentComment);
            comment.setReplyComment(replyComment);
        } else {
            throw new BusinessException(ErrorCode.INVALID_INPUT_VALUE);
        }

        comment.setStatus(CommentStatus.PUBLISHED);
        commentRepository.save(comment);

        postRepository.updateCommentCnt(post.getId(), post.getCommentCnt() + 1);

        notificationService.sendCommentMessage(comment, post);
        return comment;
    }

    public Comment findComment(Long postId, Long id) {
        Optional<Comment> findComment = commentRepository.findByIdDetails(id, postId);
        if (findComment.isEmpty()) {
            throw new UrlNotFoundException();
        }
        return findComment.get();
    }

    public Page<Comment> findCommentListByPage(Long postId, Pageable pageable, String sort) {
        return sort.equals("ASC") ? commentRepository.findByPostIdOrderByAsc(postId, pageable) : commentRepository.findByPostIdOrderByDesc(postId, pageable);
    }

    public Page<Comment> findReplyCommentListBypage(Long postId, Long id, Pageable pageable, String sort) {
        return sort.equals("ASC") ? commentRepository.findByPostIdAndCommentIdOrderByAsc(postId, id, pageable) : commentRepository.findByPostIdAndCommentIdOrderByDesc(postId, id, pageable);
    }

    public void modifyComment(Comment modifyComment, Long postId, Long memberId) {
        Optional<Member> findMember = memberRepository.findById(memberId);
        findMember.ifPresent(modifyComment::setMember);

        Optional<Post> findPost = postRepository.findById(postId);
        if (findPost.isEmpty()) {
            throw new UrlNotFoundException();
        }

        validateComment(modifyComment.getId(), postId, memberId);

        LocalDateTime modifiedAt = LocalDateTime.now();

        commentRepository.update(memberId, modifiedAt, modifyComment.getContent());
    }

    public void deleteComment(Long commentId, Long postId, Long memberId) {
        validateComment(commentId, postId, memberId);

        Optional<Post> findPost = postRepository.findById(postId);

        if (findPost.isEmpty()) {
            throw new UrlNotFoundException();
        }

        LocalDateTime deletedAt = LocalDateTime.now();

        commentRepository.delete(commentId, deletedAt, CommentStatus.DELETED);

        postRepository.updateCommentCnt(postId, findPost.get().getCommentCnt() - 1);
    }

    private void validateComment(Long commentId, Long postId, Long memberId) {
        Optional<Comment> findComment = commentRepository.findById(commentId);

        if (findComment.isEmpty()) {
            throw new UrlNotFoundException();
        }

        Comment comment = findComment.get();

        if (!Objects.equals(comment.getPost().getId(), postId) || !Objects.equals(comment.getMember().getId(), memberId)) {
            throw new BusinessException(ErrorCode.INVALID_INPUT_VALUE);
        }
    }

    public void modifyFavoriteComment(Long id, Long postId, Long memberId) {
        Optional<Post> findPost = postRepository.findById(postId);
        if (findPost.isEmpty()) {
            throw new UrlNotFoundException();
        }

        Post post = findPost.get();

        Optional<Comment> findComment = commentRepository.findById(id);
        if (findComment.isEmpty()) {
            throw new UrlNotFoundException();
        }

        Comment comment = findComment.get();

        if (!comment.getPost().getId().equals(postId)) {
            throw new BusinessException(ErrorCode.INVALID_INPUT_VALUE);
        }

        Optional<FavoriteComment> findFavoriteComment = favoriteCommentRepository.findByMemberIdAndCommentId(memberId, id);
        if (findFavoriteComment.isPresent()) {
            FavoriteComment favoriteComment = findFavoriteComment.get();
            favoriteCommentRepository.deleteById(favoriteComment.getId());
            commentRepository.updateFavoriteCnt(comment.getId(), comment.getFavoriteCnt() - 1);
        } else {
            Member member = Member.builder().id(memberId).build();
            FavoriteComment favoriteComment = FavoriteComment.builder().member(member).comment(comment).post(post).build();
            favoriteCommentRepository.save(favoriteComment);
            commentRepository.updateFavoriteCnt(comment.getId(), comment.getFavoriteCnt() + 1);
        }
    }

    public Set<FavoriteComment> findFavoriteCommentListAtPost(Long postId, Long memberId) {
        return favoriteCommentRepository.findByMemberIdAndPostId(memberId, postId);
    }

    public void permanentDelete(Long memberId) {
        int pageSize = 10;
        int page = 0;

        Page<Comment> commentListByPage;
        do {
            commentListByPage = commentRepository.findByMemberIdForWithdrawal(memberId, PageRequest.of(page, pageSize));
            List<Comment> commentList = commentListByPage.getContent();
            LocalDateTime deletedAt = LocalDateTime.now();

            commentList.forEach(
                    comment -> commentRepository.permanentDelete(comment.getId(), deletedAt)
            );
            page++;
        } while (commentListByPage.hasNext());
    }

    public void permanentDeleteFavoriteComment(Long memberId) {
        int pageSize = 10;
        int page = 0;

        Page<FavoriteComment> favoriteCommentListByPage;
        do {
            favoriteCommentListByPage = favoriteCommentRepository.findByMemberIdForWithdrawal(memberId, PageRequest.of(page, pageSize));
            List<FavoriteComment> favoriteCommentList = favoriteCommentListByPage.getContent();

            favoriteCommentList.forEach(
                    favoriteComment -> {
                        Optional<Comment> findComment = commentRepository.findByIdForWithdrawal(favoriteComment.getComment().getId());
                        if (findComment.isPresent()) {
                            Comment comment = findComment.get();
                            postRepository.updateFavoriteCnt(comment.getId(), comment.getFavoriteCnt() - 1);
                        }
                        favoriteCommentRepository.delete(favoriteComment);
                    }
            );
            page++;
        } while (favoriteCommentListByPage.hasNext());
    }
}
