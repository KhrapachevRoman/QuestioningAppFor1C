package ru.belugagroup.khrapachevrv.questioningapp.ui.polls;

import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import okhttp3.Credentials;
import ru.belugagroup.khrapachevrv.questioningapp.models.db.DaoSession;
import ru.belugagroup.khrapachevrv.questioningapp.models.db.DbQuestion;
import ru.belugagroup.khrapachevrv.questioningapp.models.db.DbQuestionDao;
import ru.belugagroup.khrapachevrv.questioningapp.models.db.DbQuestionnaire;
import ru.belugagroup.khrapachevrv.questioningapp.models.db.DbQuestionnaireDao;
import ru.belugagroup.khrapachevrv.questioningapp.models.db.DbTemplate;
import ru.belugagroup.khrapachevrv.questioningapp.models.db.DbTemplateDao;
import ru.belugagroup.khrapachevrv.questioningapp.models.network.Template;
import ru.belugagroup.khrapachevrv.questioningapp.models.prefs.PreferenceUtils;
import ru.belugagroup.khrapachevrv.questioningapp.networking.asynchronous.NetworkError;
import ru.belugagroup.khrapachevrv.questioningapp.networking.asynchronous.QuestioningService;
import ru.belugagroup.khrapachevrv.questioningapp.ui.mvp.MvpPresenter;
import rx.Subscription;
import rx.subscriptions.CompositeSubscription;

public class PollsPresenter implements MvpPresenter<PollsView> {
    private final static String TAG = "PollsPresenter";
    private final QuestioningService questioningService;
    private PollsView view;
    private final CompositeSubscription subscriptions;
    private List<DbTemplate> mTemplateList;
    private final DbTemplateDao dbTemplateDao;
    private final DbQuestionDao dbQuestionDao;
    private final DbQuestionnaireDao dbQuestionnaireDao;

    public PollsPresenter(QuestioningService questioningService, DaoSession daoSession) {
        this.questioningService = questioningService;
        this.subscriptions = new CompositeSubscription();
        this.mTemplateList = null;
        this.dbTemplateDao = daoSession.getDbTemplateDao();
        this.dbQuestionDao = daoSession.getDbQuestionDao();
        this.dbQuestionnaireDao = daoSession.getDbQuestionnaireDao();
    }

    @Override
    public void attachView(PollsView mvpView) {
        this.view = mvpView;
        updateView();
    }

    @Override
    public void detachView() {
        this.view = null;
    }

    private void updateView() {
        if (view != null) {

            if (mTemplateList != null) {
                view.partnersSuccess(mTemplateList);
            } else {
                getTemplates();
            }

        }

    }

    private void getTemplates() {

        Log.d(TAG, "-------------getTemplates------------");
        view.showWait();

        //получаем список шаблонов из БД
        mTemplateList = dbTemplateDao.queryBuilder().where(DbTemplateDao.Properties.DeleteMark.eq(false)).list();
        Log.d(TAG, "mTemplateList size = "+ mTemplateList.size());
        //если список пуст то запросим с сервера
        if (mTemplateList.isEmpty()) {
            Log.d(TAG, "dbTemplates.isEmpty()");
            getTemplatesFrom1C();
        }else{
            view.removeWait();
            Log.d(TAG, "!dbTemplates.isEmpty()");
            view.partnersSuccess(mTemplateList);
            //clearAndPushTemplateList();
        }


    }

    private void  getTemplatesFrom1C() {

        Log.d(TAG, "-------------getTemplatesFrom1C------------");

        Subscription subscription = questioningService.getTemplatesList(new QuestioningService.TemplatesListDataCallback() {
            @Override
            public void onSuccess(List<Template>  templateList) {
                Log.d(TAG, "onSuccess");

                Log.d(TAG, "save to DB");
                saveToDb(templateList);

                if (view != null) {
                    mTemplateList = dbTemplateDao.queryBuilder()
                            .where(DbTemplateDao.Properties.DeleteMark.eq(false))
                            .list();
                    view.removeWait();
                    view.partnersSuccess(mTemplateList);
                    //clearAndPushTemplateList();
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

        },  PreferenceUtils.getUrl()+"/GetTemplates/",Credentials.basic(PreferenceUtils.getUserName(), PreferenceUtils.getPassword()));

        subscriptions.add(subscription);

    }

    private void clearAndPushTemplateList(){

        List<DbTemplate> newTemplateList = new ArrayList<>();
        Long partnerId = view.getPartnersDbId();
        for (DbTemplate template: mTemplateList){

            List<DbQuestionnaire> dbQuestionnaireList = dbQuestionnaireDao
                    .queryBuilder()
                    .where(DbQuestionnaireDao.Properties.RespondentId.eq(partnerId),
                            DbQuestionnaireDao.Properties.TemplateId.eq(template.getId()))
                    .list();

            if (dbQuestionnaireList.isEmpty()){
                newTemplateList.add(template);
            }
        }
        mTemplateList = newTemplateList;
        view.partnersSuccess(mTemplateList);
    }

    private void saveToDb(List<Template> templateList){

        for (Template template : templateList) {
            Log.d(TAG, "save Template = "+ template.getTitle());

            DbTemplate foundTemplate = findTemplate(template.getId());

            if (foundTemplate==null){
                Log.d(TAG, "insert new Template");
                DbTemplate dbTemplate = new DbTemplate();
                dbTemplate.setConclusion(template.getConclusion());
                dbTemplate.setIntroduction(template.getIntroduction());
                dbTemplate.setExternalId(template.getId());
                dbTemplate.setTitle(template.getTitle());
                dbTemplate.setDeleteMark(template.getDeleteMark());
                dbTemplateDao.insert(dbTemplate);

                for (Template.Question question: template.getQuestions()) {

                    Log.d(TAG, "insert Question = "+ question.getText());

                    DbQuestion dbQuestion = new DbQuestion();
                    dbQuestion.setExternalId(question.getId());
                    dbQuestion.setSimpleId(question.getSimpleId());
                    dbQuestion.setText(question.getText());
                    dbQuestion.setHint(question.getHint());
                    dbQuestion.setIsGroup(question.getIsGroup());
                    dbQuestion.setTemplateId( dbTemplate.getId());

                    dbQuestionDao.insert(dbQuestion);

                }

            }else{
                foundTemplate.setConclusion(template.getConclusion());
                foundTemplate.setIntroduction(template.getIntroduction());
                foundTemplate.setExternalId(template.getId());
                foundTemplate.setTitle(template.getTitle());
                foundTemplate.setDeleteMark(template.getDeleteMark());
                dbTemplateDao.update(foundTemplate);
            }

        }
    }

    private DbTemplate findTemplate(String externalId){

        for(DbTemplate template : mTemplateList) {
            if(template.getExternalId().equals(externalId)) {
                return template;
            }
        }

        return null;
    }

}
