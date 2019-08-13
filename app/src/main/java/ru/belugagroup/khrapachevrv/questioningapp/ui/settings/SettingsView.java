package ru.belugagroup.khrapachevrv.questioningapp.ui.settings;


import ru.belugagroup.khrapachevrv.questioningapp.ui.mvp.MvpView;

/**
 * Created by hrapachev on 13.04.2018.
 */


interface SettingsView extends MvpView {

    String getUrl();
    String getUserName();
    String getPassword();
    String getInterviewer();

    void exchangeSuccess();
    void setProgressMessage(String message);
    boolean validate();

}
