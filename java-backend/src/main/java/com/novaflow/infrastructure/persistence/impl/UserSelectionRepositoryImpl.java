package com.novaflow.infrastructure.persistence.impl;

import com.novaflow.domain.repository.UserSelectionRepository;
import com.novaflow.domain.model.feedback.valueobject.Feedback;
import com.novaflow.domain.model.feedback.valueobject.SelectedItem;
import com.novaflow.domain.model.shared.valueobject.SelectionId;
import com.novaflow.domain.model.shared.valueobject.UserId;
import com.novaflow.domain.model.shared.valueobject.VideoId;
import com.novaflow.domain.model.shared.valueobject.RecommendationId;
import com.novaflow.infrastructure.persistence.mapper.UserSelectionMapper;
import com.novaflow.infrastructure.persistence.po.UserSelectionPO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * 用户选择记录仓储实现
 * 使用 MyBatis XML 方式操作数据库
 */
@Slf4j
@Repository
@RequiredArgsConstructor
public class UserSelectionRepositoryImpl implements UserSelectionRepository {

    private final UserSelectionMapper userSelectionMapper;

    @Override
    public Optional<com.novaflow.domain.model.feedback.UserSelection> findById(SelectionId selectionId) {
        UserSelectionPO userSelectionPO = userSelectionMapper.selectById(
                Long.parseLong(selectionId.getValue())
        );
        return Optional.ofNullable(toDomain(userSelectionPO));
    }

    @Override
    public List<com.novaflow.domain.model.feedback.UserSelection> findByUserId(UserId userId) {
        List<UserSelectionPO> userSelectionPOList = userSelectionMapper.selectByUserId(
                Long.parseLong(userId.getValue())
        );
        return userSelectionPOList.stream()
                .map(this::toDomain)
                .toList();
    }

    // Note: This method is not in the interface but is used internally
    public Optional<com.novaflow.domain.model.feedback.UserSelection> findByUserIdAndVideoId(
            UserId userId, VideoId videoId) {
        UserSelectionPO userSelectionPO = userSelectionMapper.selectByUserIdAndVideoId(
                Long.parseLong(userId.getValue()),
                Long.parseLong(videoId.getValue())
        );
        return Optional.ofNullable(toDomain(userSelectionPO));
    }

    /**
     * 根据推荐ID查找选择记录列表
     */
    @Override
    public Optional<com.novaflow.domain.model.feedback.UserSelection> findByRecommendationId(RecommendationId recommendationId) {
        List<UserSelectionPO> userSelectionPOList = userSelectionMapper.selectByRecommendationId(
                Long.parseLong(recommendationId.getValue())
        );
        return userSelectionPOList.isEmpty() ?
                Optional.empty() :
                Optional.ofNullable(toDomain(userSelectionPOList.get(0)));
    }

    // Note: This method is not in the interface but is used internally
    public List<com.novaflow.domain.model.feedback.UserSelection> findRecentByUserId(UserId userId, int limit) {
        List<UserSelectionPO> userSelectionPOList = userSelectionMapper.selectRecentByUserId(
                Long.parseLong(userId.getValue()),
                limit
        );
        return userSelectionPOList.stream()
                .map(this::toDomain)
                .toList();
    }

    @Override
    public Optional<com.novaflow.domain.model.feedback.UserSelection> findLatestByUserId(UserId userId) {
        // 获取最新的选择记录
        List<UserSelectionPO> userSelectionPOList = userSelectionMapper.selectRecentByUserId(
                Long.parseLong(userId.getValue()),
                1
        );
        return Optional.ofNullable(userSelectionPOList.isEmpty() ? null : toDomain(userSelectionPOList.get(0)));
    }

    @Override
    public boolean existsByUserIdAndRecommendationId(UserId userId, RecommendationId recommendationId) {
        return userSelectionMapper.selectByUserIdAndVideoId(
                Long.parseLong(userId.getValue()),
                Long.parseLong(recommendationId.getValue())
        ) != null;
    }

    @Override
    public long countByUserId(UserId userId) {
        return userSelectionMapper.countByUserId(Long.parseLong(userId.getValue()));
    }

    @Override
    public com.novaflow.domain.model.feedback.UserSelection save(
            com.novaflow.domain.model.feedback.UserSelection userSelection) {
        UserSelectionPO userSelectionPO = toPO(userSelection);

        if (userSelectionPO.getId() == null) {
            // 新增
            userSelectionPO.setCreatedAt(LocalDateTime.now());
            userSelectionPO.setDeleted(false);
            userSelectionMapper.insert(userSelectionPO);
        } else {
            // 更新
            userSelectionMapper.update(userSelectionPO);
        }

        return toDomain(userSelectionPO);
    }

    @Override
    public void deleteById(SelectionId selectionId) {
        userSelectionMapper.deleteById(Long.parseLong(selectionId.getValue()));
    }

    @Override
    public List<com.novaflow.domain.model.feedback.UserSelection> findAll() {
        // 默认返回前100条
        return userSelectionMapper.selectRecentByUserId(0L, 100).stream()
                .map(this::toDomain)
                .toList();
    }

    /**
     * PO 转领域对象
     */
    private com.novaflow.domain.model.feedback.UserSelection toDomain(UserSelectionPO userSelectionPO) {
        if (userSelectionPO == null) {
            return null;
        }

        // 创建 SelectedItem
        SelectedItem selectedItem = SelectedItem.of(userSelectionPO.getFoodName());

        // 创建 Feedback
        Feedback feedback = userSelectionPO.getAccepted() != null ?
                (userSelectionPO.getAccepted() ? Feedback.positive(userSelectionPO.getFeedback(), null) : Feedback.negative(userSelectionPO.getFeedback(), null)) :
                Feedback.neutral(userSelectionPO.getFeedback());

        return com.novaflow.domain.model.feedback.UserSelection.reconstruct(
                SelectionId.of(userSelectionPO.getId().toString()),
                UserId.of(userSelectionPO.getUserId().toString()),
                userSelectionPO.getRecommendationId() != null ?
                        RecommendationId.of(userSelectionPO.getRecommendationId().toString()) : null,
                selectedItem,
                feedback,
                userSelectionPO.getCreatedAt(),
                userSelectionPO.getDeleted()
        );
    }

    /**
     * 领域对象转 PO
     */
    private UserSelectionPO toPO(com.novaflow.domain.model.feedback.UserSelection userSelection) {
        if (userSelection == null) {
            return null;
        }

        UserSelectionPO userSelectionPO = new UserSelectionPO();

        if (userSelection.getId() != null) {
            userSelectionPO.setId(Long.parseLong(userSelection.getId()));
        }

        if (userSelection.getUserId() != null) {
            userSelectionPO.setUserId(Long.parseLong(userSelection.getUserId().getValue()));
        }

        if (userSelection.getRecommendationId() != null) {
            userSelectionPO.setRecommendationId(Long.parseLong(userSelection.getRecommendationId().getValue()));
        }

        // 从 SelectedItem 获取食物名称
        if (userSelection.getSelectedItem() != null) {
            userSelectionPO.setFoodName(userSelection.getSelectedItem().foodName());
        }

        // 从 Feedback 获取反馈信息
        if (userSelection.getFeedback() != null) {
            userSelectionPO.setAccepted(userSelection.getFeedback().isPositive());
            userSelectionPO.setFeedback(userSelection.getFeedback().comment());
        }

        userSelectionPO.setCreatedAt(userSelection.getCreatedAt());
        userSelectionPO.setDeleted(userSelection.isDeleted());

        return userSelectionPO;
    }
}
