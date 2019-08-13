package ru.belugagroup.khrapachevrv.questioningapp.models.network;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;


public class Questionnaire {

    @SerializedName("introduction")
    @Expose
    private String introduction;
    @SerializedName("conclusion")
    @Expose
    private String conclusion;
    @SerializedName("questions")
    @Expose
    private List<Question> questions = null;
    @SerializedName("templateId")
    @Expose
    private String templateId = "";
    @SerializedName("partnerId")
    @Expose
    private String partnerId="";
    @SerializedName("personId")
    @Expose
    private String personId="";
    @SerializedName("date")
    @Expose
    private String date="";
    @SerializedName("comment")
    @Expose
    private String comment="";

    public void setDate(String date) {
        this.date = date;
    }

    public String getTemplateId() {
        return templateId;
    }

    public void setTemplateId(String pollId) {
        this.templateId = pollId;
    }

    public String getPartnerId() {
        return partnerId;
    }

    public void setPartnerId(String partnerId) {
        this.partnerId = partnerId;
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

    public List<Question> getQuestions() {
        return questions;
    }

    public void setQuestions(List<Question> questions) {
        this.questions = questions;
    }

    public void setPersonId(String personId) {
        this.personId = personId;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }
}
