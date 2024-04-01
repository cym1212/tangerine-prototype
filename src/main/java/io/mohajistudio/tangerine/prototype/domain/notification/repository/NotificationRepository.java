package io.mohajistudio.tangerine.prototype.domain.notification.repository;

import io.mohajistudio.tangerine.prototype.domain.notification.domain.Notification;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NotificationRepository extends JpaRepository<Notification, Long> {
}
