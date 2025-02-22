package com.ssafy.health.domain.notification.entity;

import com.ssafy.health.common.entity.BaseEntity;
import com.ssafy.health.domain.account.entity.User;
import io.hypersistence.utils.hibernate.type.json.JsonType;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.Type;

import java.time.LocalDateTime;
import java.util.Map;

@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Notification extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @Enumerated(EnumType.STRING)
    private NotificationType notificationType;

    @Enumerated(EnumType.STRING)
    private NotificationStatus notificationStatus;

    private String content;

    private LocalDateTime checkedTime;

    @Type(JsonType.class)
    @Column(columnDefinition = "JSON")
    private Map<String, Object> additionalData;

    public void updateNotificationStatus(NotificationStatus notificationStatus) {
        this.notificationStatus = notificationStatus;
        if (this.checkedTime == null && notificationStatus.equals(NotificationStatus.READ)) {
            this.checkedTime = LocalDateTime.now();
        }
    }
}
