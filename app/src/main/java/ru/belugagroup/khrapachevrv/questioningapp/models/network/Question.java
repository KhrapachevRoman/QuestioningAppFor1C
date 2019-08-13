package ru.belugagroup.khrapachevrv.questioningapp.models.network;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

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
    @SerializedName("isChecked")
    @Expose
    private Boolean isChecked = false;
    @SerializedName("isGroup")
    @Expose
    private Boolean isGroup;


    public String getSimpleId() {
        return simpleId;
    }

    public void setSimpleId(String simpleId) {
        this.simpleId = simpleId;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public Boolean getIsChecked() {
        return isChecked;
    }

    public void setIsChecked(Boolean isChecked) {
        this.isChecked = isChecked;
    }

    public Boolean getIsGroup() {
        return isGroup;
    }

    public void setIsGroup(Boolean isGroup) {
        this.isGroup = isGroup;
    }

}
