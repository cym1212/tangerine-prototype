package io.mohajistudio.tangerine.prototype.infra.notification.service;

import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingException;
import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.Notification;
import io.mohajistudio.tangerine.prototype.infra.notification.dto.PushNotificationDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class FirebaseMessagingService {
    private final FirebaseMessaging firebaseMessaging;

    public void sendNotificationByToken(PushNotificationDTO notificationMessageDTO) {
        if(notificationMessageDTO.getToken() == null) {
            return;
        }

        Notification notification = Notification.builder().setTitle(notificationMessageDTO.getTitle()).setBody(notificationMessageDTO.getBody()).setImage(notificationMessageDTO.getImage()).build();
        Message message = Message.builder().setToken(notificationMessageDTO.getToken()).setNotification(notification).putAllData(notificationMessageDTO.getData()).build();

        try {
            String send = firebaseMessaging.send(message);
            log.info(send);
        } catch (FirebaseMessagingException e) {
            log.error(e.toString());
        }
    }
}
