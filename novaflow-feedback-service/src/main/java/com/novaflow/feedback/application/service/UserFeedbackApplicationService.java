package com.novaflow.feedback.application.service;

import com.novaflow.feedback.application.command.RecordSelectionCommand;
import com.novaflow.feedback.application.command.SubmitFeedbackCommand;
import com.novaflow.common.domain.event.DomainEvent;
import com.novaflow.feedback.domain.model.feedback.UserSelection;
import com.novaflow.feedback.domain.model.feedback.exception.SelectionNotFoundException;
import com.novaflow.feedback.domain.model.feedback.valueobject.Feedback;
import com.novaflow.feedback.domain.model.feedback.valueobject.SelectedItem;
import com.novaflow.common.domain.valueobject.RecommendationId;
import com.novaflow.common.domain.valueobject.SelectionId;
import com.novaflow.common.domain.valueobject.UserId;
import com.novaflow.common.domain.repository.DomainEventPublisher;
import com.novaflow.feedback.domain.repository.UserSelectionRepository;
import com.novaflow.feedback.infra.security.TokenService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 用户反馈应用服务
 * 协调用户选择和反馈收集的用例编排
 */
@Service
@RequiredArgsConstructor
public class UserFeedbackApplicationService {

    private static final Logger log = LoggerFactory.getLogger(UserFeedbackApplicationService.class);

    private final UserSelectionRepository userSelectionRepository;
    private final DomainEventPublisher eventPublisher;
    private final TokenService tokenService;

    /**
     * 记录用户选择
     */
    @Transactional
    public SelectionId recordSelection(RecordSelectionCommand command, String token) {
        // 1. 验证用户身份
        String userIdStr = tokenService.validateAccessToken(token.replace("Bearer ", ""));
        UserId userId = UserId.of(userIdStr);

        // 2. 创建选中项
        SelectedItem selectedItem = SelectedItem.of(
                command.getSelectedFood(),
                command.getSelectedPlace(),
                command.getPlaceName()
        );

        // 3. 创建推荐ID
        RecommendationId recommendationId = RecommendationId.of(command.getRecommendationId());

        // 4. 创建用户选择聚合
        UserSelection selection = UserSelection.create(userId, recommendationId, selectedItem);

        // 5. 如果有反馈，直接添加
        if (command.getFeedbackType() != null) {
            Feedback feedback = createFeedback(command.getFeedbackType(), command.getFeedbackComment(), command.getRating());
            selection.addFeedback(feedback);
        }

        // 6. 保存选择
        selection = userSelectionRepository.save(selection);

        // 7. 发布领域事件
        publishDomainEvents(selection);

        log.info("用户选择已记录: userId={}, food={}, place={}", userIdStr, command.getSelectedFood(), command.getSelectedPlace());

        return selection.getSelectionId();
    }

    /**
     * 提交反馈
     */
    @Transactional
    public void submitFeedback(SubmitFeedbackCommand command, String token) {
        // 1. 验证用户身份
        String userIdStr = tokenService.validateAccessToken(token.replace("Bearer ", ""));
        UserId userId = UserId.of(userIdStr);

        // 2. 查找选择记录
        SelectionId selectionId = SelectionId.of(command.getSelectionId());
        UserSelection selection = userSelectionRepository.findById(selectionId)
                .orElseThrow(() -> new SelectionNotFoundException("选择记录不存在"));

        // 3. 验证权限
        if (!selection.belongsToUser(userId)) {
            throw new IllegalArgumentException("无权访问此选择记录");
        }

        // 4. 创建反馈
        Feedback feedback = createFeedback(command.getFeedbackType(), command.getComment(), command.getRating());

        // 5. 添加反馈
        selection.addFeedback(feedback);

        // 6. 保存
        selection = userSelectionRepository.save(selection);

        // 7. 发布领域事件
        publishDomainEvents(selection);

        log.info("用户反馈已提交: userId={}, selectionId={}, feedbackType={}",
                userIdStr, selectionId.getValue(), command.getFeedbackType());
    }

    /**
     * 获取用户的选择历史
     */
    public List<UserSelection> getUserSelections(String token) {
        // 1. 验证用户身份
        String userIdStr = tokenService.validateAccessToken(token.replace("Bearer ", ""));
        UserId userId = UserId.of(userIdStr);

        // 2. 查询选择历史
        return userSelectionRepository.findByUserId(userId);
    }

    /**
     * 获取选择详情
     */
    public UserSelection getSelectionDetail(String selectionId, String token) {
        // 1. 验证用户身份
        String userIdStr = tokenService.validateAccessToken(token.replace("Bearer ", ""));
        UserId userId = UserId.of(userIdStr);

        // 2. 查找选择记录
        SelectionId id = SelectionId.of(selectionId);
        UserSelection selection = userSelectionRepository.findById(id)
                .orElseThrow(() -> new SelectionNotFoundException());

        // 3. 验证权限
        if (!selection.belongsToUser(userId)) {
            throw new IllegalArgumentException("无权访问此选择记录");
        }

        return selection;
    }

    /**
     * 创建反馈值对象
     */
    private Feedback createFeedback(String feedbackType, String comment, Integer rating) {
        Feedback.FeedbackType type = Feedback.FeedbackType.valueOf(feedbackType.toUpperCase());

        return switch (type) {
            case POSITIVE -> Feedback.positive(comment, rating);
            case NEUTRAL -> Feedback.neutral(comment);
            case NEGATIVE -> Feedback.negative(comment, rating);
        };
    }

    /**
     * 发布领域事件
     */
    private void publishDomainEvents(UserSelection selection) {
        for (DomainEvent event : selection.getDomainEvents()) {
            eventPublisher.publishAsync(event);
        }
        selection.clearDomainEvents();
    }
}
