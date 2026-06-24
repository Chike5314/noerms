package com.noerms.modules.notifications.service;

import com.noerms.modules.notifications.entity.NotificationLog;
import com.noerms.modules.notifications.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class NotificationService {
    private final NotificationRepository notificationRepository;

    @Transactional
    public void sendNotification(Long recipientId, String eventType, String channel, String address, String body) {
        NotificationLog log2 = NotificationLog.builder()
            .recipientId(recipientId).eventType(eventType).channel(channel)
            .recipientAddress(address).messageBody(body).status("SENT").sentAt(LocalDateTime.now()).build();
        notificationRepository.save(log2);
        log.info("Notification sent to {} via {}", address, channel);
    }

    public List<NotificationLog> getForUser(Long recipientId) {
        return notificationRepository.findByRecipientId(recipientId);
    }
}
