package ru.belugagroup.khrapachevrv.questioningapp.models.network;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Questionnaires {

    @SerializedName("questionnaires")
    @Expose
    private List<Questionnaire> questionnaireList = null;

    public List<Questionnaire> getQuestionnaireList() {
        return questionnaireList;
    }

    public void setQuestionnaireList(List<Questionnaire> questionnaireList) {
        this.questionnaireList = questionnaireList;
    }
}
