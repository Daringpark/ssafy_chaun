package com.ssafy.health.domain.notification.service;

import com.ssafy.health.common.fcm.dto.request.FcmRequestDto;
import com.ssafy.health.common.fcm.service.FcmService;
import com.ssafy.health.domain.account.entity.User;
import com.ssafy.health.domain.account.exception.UserNotFoundException;
import com.ssafy.health.domain.account.repository.UserRepository;
import com.ssafy.health.domain.battle.dto.response.BattleMatchResponseDto;
import com.ssafy.health.domain.battle.entity.BattleStatus;
import com.ssafy.health.domain.battle.repository.BattleRepository;
import com.ssafy.health.domain.battle.service.BattleReadService;
import com.ssafy.health.domain.body.BodyHistory.repository.BodyHistoryRepository;
import com.ssafy.health.domain.crew.entity.Crew;
import com.ssafy.health.domain.notification.dto.request.NotificationRequestDto;
import com.ssafy.health.domain.notification.dto.response.StatusUpdateResponseDto;
import com.ssafy.health.domain.notification.entity.Notification;
import com.ssafy.health.domain.notification.entity.NotificationStatus;
import com.ssafy.health.domain.notification.entity.NotificationType;
import com.ssafy.health.domain.notification.repository.NotificationRepository;
import com.ssafy.health.domain.quest.entity.CrewQuest;
import com.ssafy.health.domain.quest.entity.QuestType;
import com.ssafy.health.domain.quest.entity.UserQuest;
import com.ssafy.health.domain.quest.exception.QuestNotFoundException;
import com.ssafy.health.domain.quest.repository.CrewQuestRepository;
import com.ssafy.health.domain.quest.repository.UserQuestRepository;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import static com.ssafy.health.domain.notification.entity.NotificationMessage.*;

@Service
@RequiredArgsConstructor
public class NotificationWriteService {

    private final BattleRepository battleRepository;
    private final BodyHistoryRepository bodyHistoryRepository;
    private final NotificationRepository notificationRepository;
    private final UserRepository userRepository;
    private final FcmService fcmService;
    private final BattleReadService battleReadService;
    private final UserQuestRepository userQuestRepository;
    private final CrewQuestRepository crewQuestRepository;

    public void createBodySurveyNotification(NotificationRequestDto dto)
            throws ExecutionException, InterruptedException {

        Map<String, Object> additionalData = new HashMap<>();
        LocalDateTime lastSurveyedDate = null;

        try {
            lastSurveyedDate = bodyHistoryRepository.findFirstByUserIdOrderByCreatedAtDesc(dto.getUserId())
                    .orElseThrow().getCreatedAt();
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        additionalData.put("lastSurveyedDate", lastSurveyedDate);

        Notification notification = notificationBuilder(
                dto.getNotificationType(), dto.getUserId(), SURVEY.getMessage(), additionalData);
        notificationRepository.save(notification);

        sendFcmMessage(dto.getUserId(), "체형 입력 알림", SURVEY.getMessage());
    }

    public void createBattleNotification(NotificationRequestDto dto, Long battleId, Integer coinAmount)
            throws ExecutionException, InterruptedException {

        Map<String, Object> additionalData = new HashMap<>();
        BattleMatchResponseDto battleData = battleReadService.getBattleStatus(battleId);
        additionalData.put("battleDetail", battleData);

        additionalData.put("coinDetail", battleData.getBattleStatus().equals(BattleStatus.STARTED) ?
                null : CoinDetail.builder()
                        .crewCoin(200)
                        .myCoin(coinAmount)
                        .build());

        BattleStatus status = battleRepository.findById(battleId).orElseThrow().getStatus();
        String message = (status.equals(BattleStatus.STARTED) ? BATTLE_START.getMessage() : BATTLE_END.getMessage());

        Notification battleNotification = notificationBuilder(
                dto.getNotificationType(), dto.getUserId(), message, additionalData);
        notificationRepository.save(battleNotification);

        sendFcmMessage(dto.getUserId(), "배틀 알림", message);
    }

    @Data
    @Builder
    static class CoinDetail {
        private int crewCoin;
        private int myCoin;
    }

    public void createUserQuestNotification(NotificationRequestDto dto, Long questId)
            throws ExecutionException, InterruptedException {

        Map<String, Object> additionalData = new HashMap<>();

        UserQuest quest = userQuestRepository.findById(questId).orElseThrow(QuestNotFoundException::new);
        UserQuestNotification questDetail = UserQuestNotification.builder()
                .type(QuestType.INDIVIDUAL)
                .questId(questId)
                .title(quest.getQuest().getTitle())
                .coins(quest.getQuest().getCompletionCoins())
                .build();

        StringBuilder messageBuilder = new StringBuilder().append("'")
                .append(quest.getQuest().getTitle())
                .append("' ")
                .append(QUEST.getMessage());

        additionalData.put("questDetail", questDetail);
        Notification questNotification = notificationBuilder(
                dto.getNotificationType(), dto.getUserId(), messageBuilder.toString(), additionalData);
        notificationRepository.save(questNotification);

        sendFcmMessage(dto.getUserId(), "퀘스트 알림", messageBuilder.toString());
    }

    public void createCrewQuestNotification(NotificationRequestDto dto, Crew crew, Long questId)
            throws ExecutionException, InterruptedException {

        Map<String, Object> additionalData = new HashMap<>();

        CrewQuest quest = crewQuestRepository.findById(questId).orElseThrow(QuestNotFoundException::new);
        CrewQuestNotification questDetail = CrewQuestNotification.builder()
                .type(QuestType.CREW)
                .questId(questId)
                .crewId(crew.getId())
                .crewName(crew.getName())
                .title(quest.getQuest().getTitle())
                .coins(quest.getQuest().getCompletionCoins())
                .build();

        StringBuilder messageBuilder = new StringBuilder().append("크루 ").append(QUEST.getMessage());

        additionalData.put("questDetail", questDetail);
        Notification questNotification = notificationBuilder(
                dto.getNotificationType(), dto.getUserId(), messageBuilder.toString(), additionalData);
        notificationRepository.save(questNotification);

        sendFcmMessage(dto.getUserId(), "퀘스트 알림", messageBuilder.toString());
    }

    @Data
    @Builder
    static class UserQuestNotification {
        private QuestType type;
        private Long questId;
        private String title;
        private Integer coins;
    }

    @Data
    @Builder
    static class CrewQuestNotification {
        private QuestType type;
        private Long questId;
        private Long crewId;
        private String crewName;
        private String title;
        private Integer coins;
    }

    public StatusUpdateResponseDto updateNotificationStatus(NotificationStatus status, Long notificationId) {
        Notification notification = notificationRepository.findById(notificationId).orElseThrow();
        NotificationStatus previousStatus = notification.getNotificationStatus();
        notification.updateNotificationStatus(status);
        notificationRepository.save(notification);

        return StatusUpdateResponseDto.builder()
                .previousStatus(previousStatus)
                .currentStatus(status)
                .build();
    }

    public void sendFcmMessage(Long userId, String title, String body) throws ExecutionException, InterruptedException {
        User user = userRepository.findById(userId).orElseThrow(UserNotFoundException::new);
        String token = user.getDeviceToken();

        if (token != null) {
            FcmRequestDto fcmRequestDto = FcmRequestDto.builder()
                    .token(token)
                    .title(title)
                    .body(body)
                    .build();
            fcmService.sendMessageToDevice(fcmRequestDto);
        }
    }

    public Notification notificationBuilder(NotificationType notificationType, Long userId, String message) {
        return Notification.builder()
                .notificationType(notificationType)
                .notificationStatus(NotificationStatus.UNREAD)
                .content(message)
                .userId(userId)
                .build();
    }

    public Notification notificationBuilder(
            NotificationType notificationType, Long userId, String message, Map<String, Object> additionalData) {
        return Notification.builder()
                .notificationType(notificationType)
                .notificationStatus(NotificationStatus.UNREAD)
                .content(message)
                .userId(userId)
                .additionalData(additionalData)
                .build();
    }
}
