package io.mohajistudio.tangerine.prototype.domain.notification.service;

import io.mohajistudio.tangerine.prototype.domain.comment.domain.Comment;
import io.mohajistudio.tangerine.prototype.domain.member.domain.Member;
import io.mohajistudio.tangerine.prototype.domain.member.repository.MemberRepository;
import io.mohajistudio.tangerine.prototype.domain.notification.domain.Notification;
import io.mohajistudio.tangerine.prototype.domain.notification.repository.NotificationRepository;
import io.mohajistudio.tangerine.prototype.domain.post.domain.Post;
import io.mohajistudio.tangerine.prototype.global.error.exception.UrlNotFoundException;
import io.mohajistudio.tangerine.prototype.infra.notification.dto.PushNotificationDTO;
import io.mohajistudio.tangerine.prototype.infra.notification.service.FirebaseMessagingService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@RequiredArgsConstructor
public class NotificationService {
    private final MessageSource messageSource;
    private final FirebaseMessagingService firebaseMessagingService;
    private final NotificationRepository notificationRepository;
    private final MemberRepository memberRepository;

    @Transactional
    public void sendCommentMessage(Comment comment, Post post) {
        Map<Member, String> map = new HashMap<>();
        String title = messageSource.getMessage("notification.comment.title", null, LocaleContextHolder.getLocale());
        String[] messageSourceArgs = new String[]{comment.getMember().getMemberProfile().getNickname(), comment.getContent()};

        // 게시글에 댓글이 달렸을 경우
        if (!Objects.equals(comment.getMember().getId(), post.getMember().getId())) {
            String parentBody = messageSource.getMessage("notification.comment.postAuthor", messageSourceArgs, LocaleContextHolder.getLocale());
            map.put(post.getMember(), parentBody);
        }

        if (comment.getParentComment() != null) {
            // 부모 댓글에 답글이 달렸을 경우
            if (!Objects.equals(comment.getMember().getId(), comment.getParentComment().getMember().getId())) {
                String parentBody = messageSource.getMessage("notification.comment.parentAuthor", messageSourceArgs, LocaleContextHolder.getLocale());
                map.put(comment.getParentComment().getMember(), parentBody);
            }

            // 대상 댓글에 답글이 달렸을 경우
            if (!Objects.equals(comment.getMember().getId(), comment.getReplyComment().getMember().getId())) {
                String parentBody = messageSource.getMessage("notification.comment.replyAuthor", messageSourceArgs, LocaleContextHolder.getLocale());
                map.put(comment.getReplyComment().getMember(), parentBody);
            }
        }

        for (Member member : map.keySet()) {
            String body = map.get(member);

            Notification notification = Notification.builder().title(title).body(body).member(member).relatedComment(comment).relatedPost(post).relatedMember(comment.getMember()).build();
            notificationRepository.save(notification);

            int unreadNotificationsCnt = member.getUnreadNotificationsCnt() + 1;

            memberRepository.updateUnreadNotificationsCnt(member.getId(), unreadNotificationsCnt);

            PushNotificationDTO notificationMessageDTO = PushNotificationDTO.builder().title(title).body(body).token(member.getNotificationToken()).data(notification.getData()).build();
            firebaseMessagingService.sendNotificationByToken(notificationMessageDTO);
        }
    }

    @Transactional
    public Page<Notification> findNotificationListByPage(Long memberId, Pageable pageable) {
        Page<Notification> notificationListByPage = notificationRepository.findAll(memberId, pageable);

        Optional<Member> findMember = memberRepository.findById(memberId);
        if (findMember.isEmpty()) {
            throw new UrlNotFoundException();
        }

        Member member = findMember.get();

        boolean hasUnread = false;

        for (Notification notification : notificationListByPage) {
            if (!notification.isRead()) {
                hasUnread = true;
                notificationRepository.updateRead(notification.getId(), true);
                member.setUnreadNotificationsCnt(member.getUnreadNotificationsCnt() - 1);
            }
        }

        if (hasUnread) {
            memberRepository.updateUnreadNotificationsCnt(member.getId(), member.getUnreadNotificationsCnt());
        }

        return notificationListByPage;
    }

    @Transactional
    public void permanentDelete(Long memberId) {
        int pageSize = 10;
        int page = 0;

        Page<Notification> notificationListByPage;
        do {
            notificationListByPage = notificationRepository.findAllForWithdrawal(memberId, PageRequest.of(page, pageSize));
            List<Notification> notificationList = notificationListByPage.getContent();

            notificationRepository.deleteAll(notificationList);

            page++;
        } while (notificationListByPage.hasNext());

        page = 0;
        do {
            notificationListByPage = notificationRepository.findAllRelatedMemberForWithdrawal(memberId, PageRequest.of(page, pageSize));
            List<Notification> notificationList = notificationListByPage.getContent();

            notificationList.forEach(
                    notification -> notificationRepository.deleteRelatedMember(notification.getId())
            );

            page++;
        } while (notificationListByPage.hasNext());
    }
}
