package ru.belugagroup.khrapachevrv.questioningapp.ui.partners;

import android.util.Log;

import java.util.List;

import okhttp3.Credentials;
import ru.belugagroup.khrapachevrv.questioningapp.models.db.DbRespondent;
import ru.belugagroup.khrapachevrv.questioningapp.models.db.DbRespondentDao;
import ru.belugagroup.khrapachevrv.questioningapp.models.network.Partner;
import ru.belugagroup.khrapachevrv.questioningapp.models.prefs.PreferenceUtils;
import ru.belugagroup.khrapachevrv.questioningapp.networking.asynchronous.NetworkError;
import ru.belugagroup.khrapachevrv.questioningapp.networking.asynchronous.QuestioningService;
import ru.belugagroup.khrapachevrv.questioningapp.ui.mvp.MvpPresenter;
import rx.Subscription;
import rx.subscriptions.CompositeSubscription;

public class PartnersPresenter implements MvpPresenter<PartnersView> {

    private final static String TAG = "PartnersPresenter";
    private final QuestioningService questioningService;
    private PartnersView view;
    private final CompositeSubscription subscriptions;
    private List<DbRespondent> mPartnerList;
    private final DbRespondentDao dbRespondentDao;

    public PartnersPresenter(QuestioningService questioningService, DbRespondentDao dbRespondentDao) {
        this.questioningService = questioningService;
        this.subscriptions = new CompositeSubscription();
        this.mPartnerList = null;
        this.dbRespondentDao = dbRespondentDao;
    }

    @Override
    public void attachView(PartnersView mvpView) {
        this.view = mvpView;
        updateView();
    }

    @Override
    public void detachView() {
        this.view = null;
    }

    private void updateView() {
        if (view != null) {

            if (mPartnerList != null) {
                view.partnersSuccess(mPartnerList);
            } else {
                getPartners();
            }

        }

    }

    private void getPartners() {

        Log.d(TAG, "-------------getPartners------------");
        view.showWait();

        //получаем список респондентов из БД
        mPartnerList = dbRespondentDao.queryBuilder().where(DbRespondentDao.Properties.DeleteMark.eq(false)).list();

        //если список пуст то запросим с сервера
        if (mPartnerList.isEmpty()){

            Log.d(TAG, "dbRespondentList.isEmpty()");
            getPartnersListFrom1C();

        //если список не пуст, то выводим на экран
        }else{
            view.removeWait();
            Log.d(TAG, "!dbRespondentList.isEmpty()");
            view.partnersSuccess(mPartnerList);

        }

    }

    private void getPartnersListFrom1C() {

        Subscription subscription = questioningService.getPartnersList(new QuestioningService.PartnersListDataCallback() {
            @Override
            public void onSuccess(List<Partner>  partnerList) {
                Log.d(TAG, "onSuccess");

                //save to DB
                for (Partner partner : partnerList) {
                    Log.d(TAG, "save partner "+partner.getName());

                    DbRespondent foundRespondent = findRespondent(partner.getId());

                    if (foundRespondent==null){
                        Log.d(TAG, "insert");
                        DbRespondent dbRespondent = new DbRespondent();
                        dbRespondent.setExternalId(partner.getId());
                        dbRespondent.setName(partner.getName());
                        dbRespondent.setDeleteMark(partner.getDeleteMark());
                        dbRespondentDao.insert(dbRespondent);
                        Log.d(TAG, " partner getId = "+ dbRespondent.getId());
                    }else{
                        Log.d(TAG, "update");
                        foundRespondent.setExternalId(partner.getId());
                        foundRespondent.setName(partner.getName());
                        foundRespondent.setDeleteMark(partner.getDeleteMark());
                        dbRespondentDao.update(foundRespondent);
                        Log.d(TAG, " partner getId = "+ foundRespondent.getId());
                    }

                }

                if (view != null) {
                    List<DbRespondent> dbRespondentList= dbRespondentDao.queryBuilder().list();
                    mPartnerList = dbRespondentList;
                    view.removeWait();
                    view.partnersSuccess(dbRespondentList);
                }

            }

            @Override
            public void onError(NetworkError networkError) {
                Log.d(TAG, "onError");
                if (view != null) {
                    view.removeWait();
                    view.onFailure(networkError.getAppErrorMessage());
                }
            }

        }, PreferenceUtils.getUrl()+"GetPartners?Interviewer="+PreferenceUtils.getInterviewer(),Credentials.basic(PreferenceUtils.getUserName(), PreferenceUtils.getPassword()));

        subscriptions.add(subscription);

    }

    private DbRespondent findRespondent(String externalId){

        for(DbRespondent respondent : mPartnerList) {
            if(respondent.getExternalId().equals(externalId)) {
                return respondent;
            }
        }

        return null;
    }
}
