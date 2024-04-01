package io.mohajistudio.tangerine.prototype.domain.notification.service;

import io.mohajistudio.tangerine.prototype.domain.comment.domain.Comment;
import io.mohajistudio.tangerine.prototype.domain.member.domain.Member;
import io.mohajistudio.tangerine.prototype.domain.notification.domain.Notification;
import io.mohajistudio.tangerine.prototype.domain.notification.repository.NotificationRepository;
import io.mohajistudio.tangerine.prototype.domain.post.domain.Post;
import io.mohajistudio.tangerine.prototype.infra.notification.dto.NotificationMessageDTO;
import io.mohajistudio.tangerine.prototype.infra.notification.service.FirebaseMessagingService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class NotificationService {
    private final MessageSource messageSource;
    private final FirebaseMessagingService firebaseMessagingService;
    private final NotificationRepository notificationRepository;

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

            NotificationMessageDTO notificationMessageDTO = NotificationMessageDTO.builder().title(title).body(body).token(member.getNotificationToken()).data(notification.getData()).build();
            firebaseMessagingService.sendNotificationByToken(notificationMessageDTO);
        }
    }
}
