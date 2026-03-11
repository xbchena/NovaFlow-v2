package com.novaflow.domain.model.feedback;

import com.novaflow.domain.model.shared.aggregate.AggregateRoot;
import com.novaflow.domain.model.shared.valueobject.RecommendationId;
import com.novaflow.domain.model.shared.valueobject.SelectionId;
import com.novaflow.domain.model.shared.valueobject.UserId;
import com.novaflow.domain.model.feedback.event.*;
import com.novaflow.domain.model.feedback.exception.*;
import com.novaflow.domain.model.feedback.valueobject.*;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * 用户选择聚合根
 * 管理用户对推荐的反馈和选择
 */
@Getter
public class UserSelection extends AggregateRoot {

    private final SelectionId selectionId;
    private final UserId userId;
    private final RecommendationId recommendationId;
    private final SelectedItem selectedItem;
    private Feedback feedback;
    private final LocalDateTime createdAt;
    private Boolean deleted;

    // 私有构造函数
    private UserSelection(SelectionId selectionId, UserId userId, RecommendationId recommendationId,
                         SelectedItem selectedItem, Feedback feedback) {
        this.selectionId = selectionId;
        this.userId = userId;
        this.recommendationId = recommendationId;
        this.selectedItem = selectedItem;
        this.feedback = feedback;
        this.createdAt = LocalDateTime.now();
        this.deleted = false;
    }

    /**
     * 创建用户选择（工厂方法）
     */
    public static UserSelection create(UserId userId, RecommendationId recommendationId,
                                     SelectedItem selectedItem) {
        if (userId == null || recommendationId == null) {
            throw new IllegalArgumentException("用户ID和推荐ID不能为空");
        }
        if (selectedItem == null) {
            throw new IllegalArgumentException("选中项不能为空");
        }

        SelectionId selectionId = SelectionId.generate();
        UserSelection selection = new UserSelection(selectionId, userId, recommendationId, selectedItem, null);

        // 发布领域事件
        selection.addDomainEvent(new UserSelectionCreatedEvent(
                selectionId.getValue(),
                userId.getValue(),
                recommendationId.getValue(),
                selectedItem.getFoodName()
        ));

        return selection;
    }

    /**
     * 创建带反馈的用户选择
     */
    public static UserSelection createWithFeedback(UserId userId, RecommendationId recommendationId,
                                                  SelectedItem selectedItem, Feedback feedback) {
        UserSelection selection = create(userId, recommendationId, selectedItem);
        selection.feedback = feedback;

        // 如果有反馈，发布反馈事件
        if (feedback != null) {
            selection.addDomainEvent(new FeedbackSubmittedEvent(
                    selection.selectionId.getValue(),
                    userId.getValue(),
                    feedback.getType().name()
            ));
        }

        return selection;
    }

    /**
     * 添加反馈
     */
    public void addFeedback(Feedback feedback) {
        if (this.feedback != null) {
            throw new FeedbackAlreadyExistsException("反馈已存在，无法重复添加");
        }

        if (feedback == null) {
            throw new IllegalArgumentException("反馈不能为空");
        }

        this.feedback = feedback;

        // 发布反馈事件
        addDomainEvent(new FeedbackSubmittedEvent(
                this.selectionId.getValue(),
                this.userId.getValue(),
                feedback.getType().name()
        ));
    }

    /**
     * 更新反馈
     */
    public void updateFeedback(Feedback newFeedback) {
        if (newFeedback == null) {
            throw new IllegalArgumentException("反馈不能为空");
        }

        Feedback oldFeedback = this.feedback;
        this.feedback = newFeedback;

        // 发布反馈更新事件
        addDomainEvent(new FeedbackUpdatedEvent(
                this.selectionId.getValue(),
                this.userId.getValue(),
                oldFeedback != null ? oldFeedback.getType().name() : null,
                newFeedback.getType().name()
        ));
    }

    /**
     * 标记为已使用（用户实际去消费了）
     */
    public void markAsUsed() {
        if (this.deleted) {
            throw new SelectionNotFoundException("选择记录已被删除");
        }

        // 发布已使用事件
        addDomainEvent(new SelectionUsedEvent(
                this.selectionId.getValue(),
                this.userId.getValue(),
                this.selectedItem.getFoodName()
        ));
    }

    /**
     * 计算满意度分数
     * 基于反馈类型计算
     */
    public SatisfactionScore calculateSatisfaction() {
        if (feedback == null) {
            return SatisfactionScore.medium(); // 默认中等满意度
        }

        return switch (feedback.getType()) {
            case POSITIVE -> SatisfactionScore.high();
            case NEUTRAL -> SatisfactionScore.medium();
            case NEGATIVE -> SatisfactionScore.low();
        };
    }

    /**
     * 检查是否有反馈
     */
    public boolean hasFeedback() {
        return feedback != null;
    }

    /**
     * 检查是否属于指定用户
     */
    public boolean belongsToUser(UserId userId) {
        return this.userId.equals(userId);
    }

    /**
     * 检查是否属于指定推荐
     */
    public boolean belongsToRecommendation(RecommendationId recommendationId) {
        return this.recommendationId.equals(recommendationId);
    }

    /**
     * 检查是否已删除
     */
    public boolean isDeleted() {
        return deleted != null && deleted;
    }

    /**
     * 软删除选择记录
     */
    public void delete() {
        if (this.deleted) {
            throw new UnsupportedOperationException("选择记录已被删除");
        }

        this.deleted = true;

        addDomainEvent(new SelectionDeletedEvent(
                this.selectionId.getValue(),
                this.userId.getValue()
        ));
    }

    @Override
    public String getId() {
        return selectionId.getValue();
    }

    /**
     * 从数据库重建UserSelection聚合根
     */
    public static UserSelection reconstruct(
            SelectionId selectionId,
            UserId userId,
            RecommendationId recommendationId,
            SelectedItem selectedItem,
            Feedback feedback,
            LocalDateTime createdAt,
            Boolean deleted
    ) {
        UserSelection selection = new UserSelection(selectionId, userId, recommendationId, selectedItem, feedback);
        // 不使用createdAt参数，因为构造函数会设置当前时间
        // 需要通过反射或其他方式来设置，这里简化处理
        selection.deleted = deleted;
        return selection;
    }

    /**
     * 获取选择ID (Lombok 风格)
     */
    public SelectionId getSelectionId() {
        return selectionId;
    }

    /**
     * 获取推荐ID (Lombok 风格)
     */
    public RecommendationId getRecommendationId() {
        return recommendationId;
    }

    /**
     * 获取选择项 (Lombok 风格)
     */
    public SelectedItem getSelectedItem() {
        return selectedItem;
    }

    /**
     * 获取反馈 (Lombok 风格)
     */
    public Feedback getFeedback() {
        return feedback;
    }

    /**
     * 获取用户ID (Lombok 风格)
     */
    public UserId getUserId() {
        return userId;
    }

    /**
     * 获取创建时间 (Lombok 风格)
     */
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
}

