package ru.belugagroup.khrapachevrv.questioningapp.models.network;

import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Template {

    @SerializedName("id")
    @Expose
    private String id;
    @SerializedName("title")
    @Expose
    private String title;
    @SerializedName("introduction")
    @Expose
    private String introduction;
    @SerializedName("conclusion")
    @Expose
    private String conclusion;
    @SerializedName("deleteMark")
    @Expose
    private Boolean deleteMark;
    @SerializedName("questions")
    @Expose
    private List<Question> questions = null;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getIntroduction() {
        return introduction;
    }

    public void setIntroduction(String introduction) {
        this.introduction = introduction;
    }

    public String getConclusion() {
        return conclusion;
    }

    public void setConclusion(String conclusion) {
        this.conclusion = conclusion;
    }

    public Boolean getDeleteMark() {
        return deleteMark;
    }

    public void setDeleteMark(Boolean deleteMark) {
        this.deleteMark = deleteMark;
    }

    public List<Question> getQuestions() {
        return questions;
    }

    public void setQuestions(List<Question> questions) {
        this.questions = questions;
    }

    public class Question {

        @SerializedName("id")
        @Expose
        private String id;
        @SerializedName("simpleId")
        @Expose
        private String simpleId;
        @SerializedName("text")
        @Expose
        private String text;
        @SerializedName("hint")
        @Expose
        private String hint;
        @SerializedName("parent")
        @Expose
        private String parent;
        @SerializedName("isGroup")
        @Expose
        private Boolean isGroup;

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getSimpleId() {
            return simpleId;
        }

        public void setSimpleId(String simpleId) {
            this.simpleId = simpleId;
        }

        public String getText() {
            return text;
        }

        public void setText(String text) {
            this.text = text;
        }

        public String getHint() {
            return hint;
        }

        public void setHint(String hint) {
            this.hint = hint;
        }

        public String getParent() {
            return parent;
        }

        public void setParent(String parent) {
            this.parent = parent;
        }

        public Boolean getIsGroup() {
            return isGroup;
        }

        public void setIsGroup(Boolean isGroup) {
            this.isGroup = isGroup;
        }

    }

}