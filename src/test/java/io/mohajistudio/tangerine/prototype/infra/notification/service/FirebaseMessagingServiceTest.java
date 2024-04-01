package io.mohajistudio.tangerine.prototype.infra.notification.service;

import io.mohajistudio.tangerine.prototype.infra.notification.dto.NotificationMessageDTO;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.MessageSource;
import org.springframework.test.context.ActiveProfiles;

import java.util.HashMap;
import java.util.Locale;

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
        //given
        NotificationMessageDTO notificationMessageDTO = NotificationMessageDTO.builder().title("Trenvel Push Message Test").body("ㅎㅇㅎㅇ").token("dHfEScUcQk7anA3h_JnK6k:APA91bEtUqkfYkm9JRxy0WiCjAp47u1bfimiWB1oTvnPdEDb945ClRc8VVqOBLPUb-BOySXebgqZjov2owoyGu7VtYFYHvaPF3ffodjhzSRf0CPfnw32AzOMVR4eZIPuClIUFPMM7ian").data(new HashMap<>()).build();
        //when
        firebaseMessagingService.sendNotificationByToken(notificationMessageDTO);
        //then
    }

    @Test
    void printMessageSource() {
        String message = messageSource.getMessage("notification.comment.title", null, Locale.getDefault());
        log.info(message);
    }
}