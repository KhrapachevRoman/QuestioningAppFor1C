package ru.belugagroup.khrapachevrv.questioningapp.ui.mvp;



public interface MvpView  {

    void showWait();

    void removeWait();

    void onFailure(String appErrorMessage);
}
