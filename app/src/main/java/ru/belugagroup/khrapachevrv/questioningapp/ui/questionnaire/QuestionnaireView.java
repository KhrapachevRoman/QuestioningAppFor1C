package ru.belugagroup.khrapachevrv.questioningapp.ui.questionnaire;

import java.util.List;

import ru.belugagroup.khrapachevrv.questioningapp.models.db.DbQuestionForQuestionnaire;
import ru.belugagroup.khrapachevrv.questioningapp.models.db.DbTemplate;
import ru.belugagroup.khrapachevrv.questioningapp.ui.mvp.MvpView;


interface QuestionnaireView extends MvpView {

    Long getTemplateDbId();
    Long getPartnerDbId();
    Long getQuestionnaireDbId();
    Long getPersonDbId();
    String getComment();
    Boolean getIsHistory();

    void templateSuccess(DbTemplate template, List<DbQuestionForQuestionnaire> answerList);
    void createSuccess();
    void deleteSuccess();
    void updateSuccess();

}