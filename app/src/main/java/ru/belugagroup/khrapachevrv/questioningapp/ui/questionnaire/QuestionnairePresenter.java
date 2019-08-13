package ru.belugagroup.khrapachevrv.questioningapp.ui.questionnaire;

import android.util.Log;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import ru.belugagroup.khrapachevrv.questioningapp.models.db.DaoSession;
import ru.belugagroup.khrapachevrv.questioningapp.models.db.DbPerson;
import ru.belugagroup.khrapachevrv.questioningapp.models.db.DbQuestionForQuestionnaire;
import ru.belugagroup.khrapachevrv.questioningapp.models.db.DbQuestion;
import ru.belugagroup.khrapachevrv.questioningapp.models.db.DbQuestionForQuestionnaireDao;
import ru.belugagroup.khrapachevrv.questioningapp.models.db.DbQuestionnaire;
import ru.belugagroup.khrapachevrv.questioningapp.models.db.DbQuestionnaireDao;
import ru.belugagroup.khrapachevrv.questioningapp.models.db.DbTemplate;
import ru.belugagroup.khrapachevrv.questioningapp.models.db.DbTemplateDao;
import ru.belugagroup.khrapachevrv.questioningapp.ui.mvp.MvpPresenter;

public class QuestionnairePresenter implements MvpPresenter<QuestionnaireView> {
    private final static String TAG = "QuestionnairePresenter";
    private QuestionnaireView view;
    private final DbTemplateDao dbTemplateDao;
    private final DbQuestionnaireDao dbQuestionnaireDao;
    private final DbQuestionForQuestionnaireDao dbQuestionForQuestionnaireDao;
    private DbTemplate template;
    private List<DbQuestionForQuestionnaire> answerList;

    public QuestionnairePresenter(DaoSession daoSession) {
        this.template = null;
        this.dbTemplateDao = daoSession.getDbTemplateDao();
        this.dbQuestionnaireDao = daoSession.getDbQuestionnaireDao();
        this.dbQuestionForQuestionnaireDao = daoSession.getDbQuestionForQuestionnaireDao();
        this.answerList = new ArrayList<>();
    }

    @Override
    public void attachView(QuestionnaireView mvpView) {
        this.view = mvpView;
        updateView();
    }

    @Override
    public void detachView() {
        this.view = null;
    }

    private void updateView() {
        if (view != null) {

            if (template != null) {
                view.templateSuccess(template, answerList);
            } else {
                getTemplate();
            }

        }

    }

    private void getTemplate() {

        Log.d(TAG, "-------------getTemplate------------");

        if (view.getIsHistory()) {

            DbQuestionnaire dbQuestionnaire = findQuestionnaireById();

            if (dbQuestionnaire != null) {
                template = dbQuestionnaire.getTemplate();
                answerList = dbQuestionnaire.getAnswerList();

            }

        } else {
            List<DbTemplate> dbTemplateList = dbTemplateDao.queryBuilder().where(DbTemplateDao.Properties.Id.eq(view.getTemplateDbId())).limit(1).list();

            template = dbTemplateList.get(0);

            createAnswerList();
        }

        view.templateSuccess(template, answerList);
    }

    private DbQuestionnaire findQuestionnaireById() {

        List<DbQuestionnaire> dbQuestionnaireList = dbQuestionnaireDao
                .queryBuilder()
                .where(DbQuestionnaireDao.Properties.Id.eq(view.getQuestionnaireDbId()))
                .limit(1)
                .list();

        return dbQuestionnaireList.get(0);

    }

    private void createAnswerList() {

        for (DbQuestion question : template.getQuestionList()) {

            DbQuestionForQuestionnaire answer = new DbQuestionForQuestionnaire();
            answer.setChecked(false);
            answer.setQuestion(question);
            answer.setQuestionId(question.getId());
            answerList.add(answer);

        }

    }

    void createQuestionnaire() {

        Log.d(TAG, "-------------createQuestionnaire------------");

        DbQuestionnaire dbQuestionnaire = new DbQuestionnaire();
        Date currentTime = Calendar.getInstance().getTime();
        dbQuestionnaire.setDateInMillis(currentTime.getTime());
        dbQuestionnaire.setRespondentId(view.getPartnerDbId());
        dbQuestionnaire.setTemplateId(view.getTemplateDbId());
        dbQuestionnaire.setComment(view.getComment());
        if (view.getPersonDbId() != 0L) {
            dbQuestionnaire.setPersonId(view.getPersonDbId());
        }
        dbQuestionnaireDao.insert(dbQuestionnaire);

        for (DbQuestionForQuestionnaire answer : answerList) {

            Log.d(TAG, "answer  " + answer.getQuestion().getText() + " = " + answer.getChecked());
            DbQuestionForQuestionnaire dbQuestionForQuestionnaire = new DbQuestionForQuestionnaire();
            dbQuestionForQuestionnaire.setQuestionnaireId(dbQuestionnaire.getId());
            dbQuestionForQuestionnaire.setQuestionId(answer.getQuestionId());
            dbQuestionForQuestionnaire.setChecked(answer.getChecked());
            dbQuestionForQuestionnaireDao.insert(dbQuestionForQuestionnaire);
        }

        view.createSuccess();

    }

    void updateQuestionnaire() {
        Log.d(TAG, "-------------updateQuestionnaire------------");

        DbQuestionnaire dbQuestionnaire = findQuestionnaireById();

        if (dbQuestionnaire != null) {

            //обновляем анкету
            Date currentTime = Calendar.getInstance().getTime();
            dbQuestionnaire.setDateInMillis(currentTime.getTime());
            dbQuestionnaire.setComment(view.getComment());
            if (view.getPersonDbId() != 0L) {
                Log.d(TAG, "view.getPersonDbId() != 0L)");
                dbQuestionnaire.setPersonId(view.getPersonDbId());
            }else{
                Log.d(TAG, "view.getPersonDbId() = 0L)");
                dbQuestionnaire.setPersonId(view.getPersonDbId());

            }

            //удаляем овтеты
            for (DbQuestionForQuestionnaire question : dbQuestionnaire.getAnswerList()) {
                question.delete();
            }

            //записываем заново
            for (DbQuestionForQuestionnaire answer : answerList) {

                Log.d(TAG, "answer  " + answer.getQuestion().getText() + " = " + answer.getChecked());
                DbQuestionForQuestionnaire dbQuestionForQuestionnaire = new DbQuestionForQuestionnaire();
                dbQuestionForQuestionnaire.setQuestionnaireId(dbQuestionnaire.getId());
                dbQuestionForQuestionnaire.setQuestionId(answer.getQuestionId());
                dbQuestionForQuestionnaire.setChecked(answer.getChecked());
                dbQuestionForQuestionnaireDao.insert(dbQuestionForQuestionnaire);
            }

            dbQuestionnaireDao.save(dbQuestionnaire);

        }

        view.updateSuccess();

    }

    void deleteQuestionnaire() {
        Log.d(TAG, "-------------deleteQuestionnaire------------");

        //dbQuestionnaireDao.deleteByKey(view.getQuestionnaireDbId());

        DbQuestionnaire dbQuestionnaire = findQuestionnaireById();

        if (dbQuestionnaire != null) {

            //удаляем овтеты
            for (DbQuestionForQuestionnaire question : dbQuestionnaire.getAnswerList()) {
                question.delete();
            }
            //удаляем анкету
            dbQuestionnaire.delete();

        }

        view.deleteSuccess();
    }

    void setItemChecked(int position, boolean checked) {

        answerList.get(position).setChecked(checked);

        Log.d(TAG, "setItemChecked "
                + answerList.get(position).getQuestion().getText()
                + " = "
                + answerList.get(position).getChecked());
    }
}
