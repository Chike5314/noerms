package com.noerms.modules.notifications.entity;

import com.noerms.core.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "notification_logs")
@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class NotificationLog extends BaseEntity {
    @Column(name = "recipient_id", nullable = false)
    private Long recipientId;
    @Column(name = "event_type", nullable = false, length = 100)
    private String eventType;
    @Column(nullable = false, length = 20)
    private String channel;
    @Column(name = "recipient_address", nullable = false, length = 255)
    private String recipientAddress;
    @Column(name = "message_body")
    private String messageBody;
    @Column(length = 50)
    private String status = "PENDING";
    @Column(name = "retry_count")
    private Integer retryCount = 0;
    @Column(name = "error_message")
    private String errorMessage;
    @Column(name = "sent_at")
    private LocalDateTime sentAt;
}
