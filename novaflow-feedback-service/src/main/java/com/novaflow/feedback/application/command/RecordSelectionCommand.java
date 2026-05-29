package com.novaflow.feedback.application.command;

/**
 * 记录选择命令
 */
public class RecordSelectionCommand {
    private final String recommendationId;
    private final String selectedFood;
    private final String selectedPlace;
    private final String placeName;
    private final String feedbackType; // positive, neutral, negative
    private final String feedbackComment;
    private final Integer rating;

    // Private constructor
    private RecordSelectionCommand(Builder builder) {
        this.recommendationId = builder.recommendationId;
        this.selectedFood = builder.selectedFood;
        this.selectedPlace = builder.selectedPlace;
        this.placeName = builder.placeName;
        this.feedbackType = builder.feedbackType;
        this.feedbackComment = builder.feedbackComment;
        this.rating = builder.rating;
    }

    // Getters
    public String getRecommendationId() {
        return recommendationId;
    }

    public String getSelectedFood() {
        return selectedFood;
    }

    public String getSelectedPlace() {
        return selectedPlace;
    }

    public String getPlaceName() {
        return placeName;
    }

    public String getFeedbackType() {
        return feedbackType;
    }

    public String getFeedbackComment() {
        return feedbackComment;
    }

    public Integer getRating() {
        return rating;
    }

    // Builder
    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private String recommendationId;
        private String selectedFood;
        private String selectedPlace;
        private String placeName;
        private String feedbackType;
        private String feedbackComment;
        private Integer rating;

        public Builder recommendationId(String recommendationId) {
            this.recommendationId = recommendationId;
            return this;
        }

        public Builder selectedFood(String selectedFood) {
            this.selectedFood = selectedFood;
            return this;
        }

        public Builder selectedPlace(String selectedPlace) {
            this.selectedPlace = selectedPlace;
            return this;
        }

        public Builder placeName(String placeName) {
            this.placeName = placeName;
            return this;
        }

        public Builder feedbackType(String feedbackType) {
            this.feedbackType = feedbackType;
            return this;
        }

        public Builder feedbackComment(String feedbackComment) {
            this.feedbackComment = feedbackComment;
            return this;
        }

        public Builder rating(Integer rating) {
            this.rating = rating;
            return this;
        }

        public RecordSelectionCommand build() {
            return new RecordSelectionCommand(this);
        }
    }
}
