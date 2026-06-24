package com.noerms.modules.notifications.repository;

import com.noerms.modules.notifications.entity.NotificationLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface NotificationRepository extends JpaRepository<NotificationLog, Long> {
    List<NotificationLog> findByRecipientId(Long recipientId);
    List<NotificationLog> findByStatus(String status);
    List<NotificationLog> findByStatusAndRetryCountLessThan(String status, int maxRetries);
}
