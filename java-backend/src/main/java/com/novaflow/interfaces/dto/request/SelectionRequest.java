package com.novaflow.interfaces.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * 选择请求DTO
 */
@Data
public class SelectionRequest {

    /**
     * 推荐ID
     */
    @NotBlank(message = "推荐ID不能为空")
    private String recommendationId;

    /**
     * 选择的食物
     */
    @NotBlank(message = "选择的食物不能为空")
    private String selectedFood;

    /**
     * 选择的地点
     */
    private String selectedPlace;

    /**
     * 反馈类型：positive, neutral, negative
     */
    private String feedback;
}
