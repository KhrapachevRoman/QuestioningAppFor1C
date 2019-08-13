package ru.belugagroup.khrapachevrv.questioningapp.ui.main;

import java.util.List;

import ru.belugagroup.khrapachevrv.questioningapp.models.db.DbQuestionnaire;
import ru.belugagroup.khrapachevrv.questioningapp.ui.mvp.MvpView;


interface MainView extends MvpView {

    void questionnaireSuccess(List<DbQuestionnaire> questionnaireList);
    void exchangeSuccess();
    void setProgressMessage(String message);
}