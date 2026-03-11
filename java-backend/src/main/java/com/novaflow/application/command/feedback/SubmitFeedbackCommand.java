package com.novaflow.application.command.feedback;

/**
 * 提交反馈命令
 */
public class SubmitFeedbackCommand {
    private final String selectionId;
    private final String feedbackType; // positive, neutral, negative
    private final String comment;
    private final Integer rating;

    // Private constructor
    private SubmitFeedbackCommand(Builder builder) {
        this.selectionId = builder.selectionId;
        this.feedbackType = builder.feedbackType;
        this.comment = builder.comment;
        this.rating = builder.rating;
    }

    // Getters
    public String getSelectionId() {
        return selectionId;
    }

    public String getFeedbackType() {
        return feedbackType;
    }

    public String getComment() {
        return comment;
    }

    public Integer getRating() {
        return rating;
    }

    // Builder
    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private String selectionId;
        private String feedbackType;
        private String comment;
        private Integer rating;

        public Builder selectionId(String selectionId) {
            this.selectionId = selectionId;
            return this;
        }

        public Builder feedbackType(String feedbackType) {
            this.feedbackType = feedbackType;
            return this;
        }

        public Builder comment(String comment) {
            this.comment = comment;
            return this;
        }

        public Builder rating(Integer rating) {
            this.rating = rating;
            return this;
        }

        public SubmitFeedbackCommand build() {
            return new SubmitFeedbackCommand(this);
        }
    }
}
