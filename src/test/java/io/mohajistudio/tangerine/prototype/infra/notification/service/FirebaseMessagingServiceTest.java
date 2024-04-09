package io.mohajistudio.tangerine.prototype.infra.notification.service;

import io.mohajistudio.tangerine.prototype.infra.notification.dto.PushNotificationDTO;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.MessageSource;
import org.springframework.test.context.ActiveProfiles;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

@Slf4j
@SpringBootTest
@ActiveProfiles("local")
class FirebaseMessagingServiceTest {
    @Autowired
    private FirebaseMessagingService firebaseMessagingService;
    @Autowired
    private MessageSource messageSource;

    @Test
    void sendPushNotification() {
        Map<String, String> data = new HashMap<>();
        data.put("member", "3");
        data.put("relatedPost", "3");
        data.put("relatedComment", "");
        data.put("relatedMember", "3");
        //given
        PushNotificationDTO notificationMessageDTO = PushNotificationDTO.builder().title("댓글 작성").body("Dochi님이 회원님의 댓글에 답글을 작성했습니다: 테스트 댓글").token("dHfEScUcQk7anA3h_JnK6k:APA91bEtUqkfYkm9JRxy0WiCjAp47u1bfimiWB1oTvnPdEDb945ClRc8VVqOBLPUb-BOySXebgqZjov2owoyGu7VtYFYHvaPF3ffodjhzSRf0CPfnw32AzOMVR4eZIPuClIUFPMM7ian").data(data).build();
        //when
        firebaseMessagingService.sendNotificationByToken(notificationMessageDTO);
        //then
    }

    @Test
    void printMessageSource() {
        String message = messageSource.getMessage("notification.comment.title", null, Locale.getDefault());
    }
}