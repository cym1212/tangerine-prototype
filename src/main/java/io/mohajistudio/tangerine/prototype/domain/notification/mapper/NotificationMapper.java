package io.mohajistudio.tangerine.prototype.domain.notification.mapper;

import io.mohajistudio.tangerine.prototype.domain.notification.domain.Notification;
import io.mohajistudio.tangerine.prototype.domain.notification.dto.NotificationDTO;
import org.mapstruct.Mapper;

@Mapper
public interface NotificationMapper {
    NotificationDTO toDTO(Notification notification);
}
