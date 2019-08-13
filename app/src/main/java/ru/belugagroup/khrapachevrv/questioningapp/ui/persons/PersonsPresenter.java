package ru.belugagroup.khrapachevrv.questioningapp.ui.persons;

import android.util.Log;

import java.util.List;

import okhttp3.Credentials;
import ru.belugagroup.khrapachevrv.questioningapp.models.db.DbPerson;
import ru.belugagroup.khrapachevrv.questioningapp.models.db.DbPersonDao;
import ru.belugagroup.khrapachevrv.questioningapp.models.db.DbRespondent;
import ru.belugagroup.khrapachevrv.questioningapp.models.db.DbRespondentDao;
import ru.belugagroup.khrapachevrv.questioningapp.models.network.Partner;
import ru.belugagroup.khrapachevrv.questioningapp.models.prefs.PreferenceUtils;
import ru.belugagroup.khrapachevrv.questioningapp.networking.asynchronous.NetworkError;
import ru.belugagroup.khrapachevrv.questioningapp.networking.asynchronous.QuestioningService;
import ru.belugagroup.khrapachevrv.questioningapp.ui.mvp.MvpPresenter;
import rx.Subscription;
import rx.subscriptions.CompositeSubscription;

public class PersonsPresenter implements MvpPresenter<PersonsView> {

    private final static String TAG = "PartnersPresenter";
    private PersonsView view;
    private List<DbPerson> personList;
    private final DbPersonDao dbPersonDao;

    public PersonsPresenter(DbPersonDao dbPersonDao) {
        this.personList = null;
        this.dbPersonDao = dbPersonDao;
    }

    @Override
    public void attachView(PersonsView mvpView) {
        this.view = mvpView;
        updateView();
    }

    @Override
    public void detachView() {
        this.view = null;
    }

    private void updateView() {
        if (view != null) {

            if (personList != null) {
                view.personsSuccess(personList);
            } else {
                getData();
            }

        }

    }

    private void getData() {

        Log.d(TAG, "-------------getData------------");
        view.showWait();

        //получаем список респондентов из БД
        personList = dbPersonDao.queryBuilder().where(DbPersonDao.Properties.DeleteMark.eq(false)).list();

        //если список пуст то запросим с сервера
        if (personList.isEmpty()){
            view.removeWait();
            Log.d(TAG, "personList.isEmpty()");


        //если список не пуст, то выводим на экран
        }else{
            view.removeWait();
            Log.d(TAG, "!personList.isEmpty()");
            view.personsSuccess(personList);

        }

    }

}
