package ru.belugagroup.khrapachevrv.questioningapp.ui.settings;

import android.support.annotation.NonNull;
import android.util.Log;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import okhttp3.Credentials;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import ru.belugagroup.khrapachevrv.questioningapp.models.db.DaoSession;
import ru.belugagroup.khrapachevrv.questioningapp.models.db.DbPerson;
import ru.belugagroup.khrapachevrv.questioningapp.models.db.DbPersonDao;
import ru.belugagroup.khrapachevrv.questioningapp.models.db.DbQuestion;
import ru.belugagroup.khrapachevrv.questioningapp.models.db.DbQuestionDao;
import ru.belugagroup.khrapachevrv.questioningapp.models.db.DbQuestionForQuestionnaire;
import ru.belugagroup.khrapachevrv.questioningapp.models.db.DbQuestionnaire;
import ru.belugagroup.khrapachevrv.questioningapp.models.db.DbQuestionnaireDao;
import ru.belugagroup.khrapachevrv.questioningapp.models.db.DbRespondent;
import ru.belugagroup.khrapachevrv.questioningapp.models.db.DbRespondentDao;
import ru.belugagroup.khrapachevrv.questioningapp.models.db.DbTemplate;
import ru.belugagroup.khrapachevrv.questioningapp.models.db.DbTemplateDao;
import ru.belugagroup.khrapachevrv.questioningapp.models.network.Partner;
import ru.belugagroup.khrapachevrv.questioningapp.models.network.Person;
import ru.belugagroup.khrapachevrv.questioningapp.models.network.Question;
import ru.belugagroup.khrapachevrv.questioningapp.models.network.Questionnaire;
import ru.belugagroup.khrapachevrv.questioningapp.models.network.Questionnaires;
import ru.belugagroup.khrapachevrv.questioningapp.models.network.Template;
import ru.belugagroup.khrapachevrv.questioningapp.models.prefs.PreferenceUtils;
import ru.belugagroup.khrapachevrv.questioningapp.networking.asynchronous.QuestioningService;
import ru.belugagroup.khrapachevrv.questioningapp.networking.synchronous.NetworkDataService;
import ru.belugagroup.khrapachevrv.questioningapp.networking.synchronous.RetrofitInstance;
import ru.belugagroup.khrapachevrv.questioningapp.ui.mvp.MvpPresenter;
import rx.subscriptions.CompositeSubscription;

/**
 * Created by hrapachev on 10.05.2018.
 */


public class SettingsPresenter implements MvpPresenter<SettingsView> {

    private final static String TAG = "SettingsPresenter";

    private final QuestioningService questioningService;
    private SettingsView view;
    private final CompositeSubscription subscriptions;
    private final DbQuestionnaireDao dbQuestionnaireDao;
    private final DbRespondentDao dbRespondentDao;
    private final DbTemplateDao dbTemplateDao;
    private final DbQuestionDao dbQuestionDao;
    private final DbPersonDao dbPersonDao;


    public SettingsPresenter(QuestioningService questioningService, DaoSession daoSession) {
        this.questioningService = questioningService;
        this.subscriptions = new CompositeSubscription();
        this.dbQuestionnaireDao = daoSession.getDbQuestionnaireDao();
        this.dbRespondentDao = daoSession.getDbRespondentDao();
        this.dbTemplateDao = daoSession.getDbTemplateDao();
        this.dbQuestionDao = daoSession.getDbQuestionDao();
        this.dbPersonDao = daoSession.getDbPersonDao();

    }

    public void attachView(SettingsView view) {
        this.view = view;
    }

    public void detachView() {
        this.view = null;
    }

    public void exchange() {

        if (!view.validate()) {
            view.onFailure("Не все поля заполнены!");
            return;
        }
        Log.d(TAG, "-------------exchange------------");
        view.showWait();
        if (view != null) {
            view.setProgressMessage("Проверяем персональные данные...");
        }

        getCheck();

    }

    private void getCheck() {

        Log.d(TAG, "-------------getCheck------------");

        /** Create handle for the RetrofitInstance interface*/
        NetworkDataService service = RetrofitInstance.getRetrofitInstance().create(NetworkDataService.class);

        /** Call the method with parameter in the interface to get the notice data*/
        Call<ResponseBody> call = service.getCheck(view.getUrl() + "/GetCheck?Interviewer=" + view.getInterviewer(),
                Credentials.basic(view.getUserName(), view.getPassword()));

        Log.i(TAG, "getCheck try call ");

        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(@NonNull Call<ResponseBody> call,
                                   @NonNull Response<ResponseBody> response) {
                Log.v(TAG, "success");
                int responseCode = response.code();
                Log.i(TAG, "responseCode = " + responseCode);

                if (responseCode == 200) {
                    PreferenceUtils.saveUserName(view.getUserName());
                    PreferenceUtils.savePassword(view.getPassword());
                    PreferenceUtils.saveInterviewer(view.getInterviewer());
                    PreferenceUtils.saveUrl(view.getUrl());

                    getPartnersListFrom1C();
                } else {
                    if (view != null) {
                        view.removeWait();
                        Log.d(TAG, "onError");
                        view.onFailure("Не удалось проверить персональные данные.");
                    }
                }

            }

            @Override
            public void onFailure(@NonNull Call<ResponseBody> call, @NonNull Throwable t) {
                if (view != null) {
                    view.removeWait();
                    Log.d(TAG, "onError getMessage = " + t.toString());
                    view.onFailure(t.toString());
                }
            }
        });

    }

    private void getPartnersListFrom1C() {

        Log.d(TAG, "-------------getPartnersListFrom1C------------");

        if (view != null) {
            view.setProgressMessage("Получаем партнёров...");
        }

        /** Create handle for the RetrofitInstance interface*/
        NetworkDataService service = RetrofitInstance.getRetrofitInstance().create(NetworkDataService.class);

        /** Call the method with parameter in the interface to get the notice data*/
        String Url = PreferenceUtils.getUrl();
        String Interviewer = PreferenceUtils.getInterviewer();
        String UserName = PreferenceUtils.getUserName();
        String Password = PreferenceUtils.getPassword();
        Log.i(TAG, Url + "GetPartners?Interviewer=" + Interviewer + Credentials.basic(UserName, Password));
        Call<List<Partner>> call = service.getPartners(Url + "/GetPartners?Interviewer=" + Interviewer, Credentials.basic(UserName, Password));

        call.enqueue(new Callback<List<Partner>>() {
            @Override
            public void onResponse(@NonNull Call<List<Partner>> call,
                                   @NonNull Response<List<Partner>> response) {
                Log.v(TAG, "success");
                int responseCode = response.code();
                Log.i(TAG, "responseCode = " + response.code());

                if (responseCode == 200) {
                    List<Partner> partnerList = response.body();

                    savePartnersToDb(partnerList);

                    getPersonsListFrom1C();
                } else {
                    if (view != null) {
                        view.removeWait();
                        Log.d(TAG, "onError");
                        view.onFailure("Не удалось получить партнёров.");
                    }
                }

            }

            @Override
            public void onFailure(@NonNull Call<List<Partner>> call, @NonNull Throwable t) {
                if (view != null) {
                    view.removeWait();
                    Log.d(TAG, "onError getMessage = " + t.toString());
                    view.onFailure(t.toString());
                }

            }
        });

    }

    private void savePartnersToDb(List<Partner> partnerList) {
        final List<DbRespondent> mPartnerList = dbRespondentDao.queryBuilder().list();

        if (partnerList != null) {

            for (Partner partner : partnerList) {
                Log.d(TAG, "save partner " + partner.getName());

                DbRespondent foundRespondent = findRespondent(mPartnerList, partner.getId());

                if (foundRespondent == null) {
                    Log.d(TAG, "insert");
                    DbRespondent dbRespondent = new DbRespondent();
                    dbRespondent.setExternalId(partner.getId());
                    dbRespondent.setName(partner.getName());
                    dbRespondent.setDeleteMark(partner.getDeleteMark());
                    dbRespondentDao.insert(dbRespondent);
                    Log.d(TAG, " dbRespondent getId = " + dbRespondent.getId());
                } else {
                    Log.d(TAG, "update");
                    foundRespondent.setExternalId(partner.getId());
                    foundRespondent.setName(partner.getName());
                    foundRespondent.setDeleteMark(partner.getDeleteMark());
                    dbRespondentDao.update(foundRespondent);
                    Log.d(TAG, " dbRespondent getId = " + foundRespondent.getId());
                }

            }
        }
    }

    private DbRespondent findRespondent(List<DbRespondent> dbRespondentList, String externalId) {

        for (DbRespondent respondent : dbRespondentList) {
            if (respondent.getExternalId().equals(externalId)) {
                return respondent;
            }
        }

        return null;
    }

    private void getPersonsListFrom1C() {

        Log.d(TAG, "-------------getPersonsListFrom1C------------");

        if (view != null) {
            view.setProgressMessage("Получаем физические лица...");
        }

        /** Create handle for the RetrofitInstance interface*/
        NetworkDataService service = RetrofitInstance.getRetrofitInstance().create(NetworkDataService.class);

        /** Call the method with parameter in the interface to get the notice data*/
        String Url = PreferenceUtils.getUrl();
        String Interviewer = PreferenceUtils.getInterviewer();
        String UserName = PreferenceUtils.getUserName();
        String Password = PreferenceUtils.getPassword();
        Call<List<Person>> call = service.getPersons(Url + "/GetPersons?Interviewer=" + Interviewer, Credentials.basic(UserName, Password));

        call.enqueue(new Callback<List<Person>>() {
            @Override
            public void onResponse(@NonNull Call<List<Person>> call,
                                   @NonNull Response<List<Person>> response) {
                Log.v(TAG, "success");
                int responseCode = response.code();
                Log.i(TAG, "responseCode = " + response.code());

                if (responseCode == 200) {
                    List<Person> personList = response.body();

                    savePersonToDb(personList);

                    getTemplatesFrom1C();
                } else {
                    if (view != null) {
                        view.removeWait();
                        Log.d(TAG, "onError");
                        view.onFailure("Не удалось получить физ. лица.");
                    }
                }

            }

            @Override
            public void onFailure(@NonNull Call<List<Person>> call, @NonNull Throwable t) {
                if (view != null) {
                    view.removeWait();
                    Log.d(TAG, "onError getMessage = " + t.toString());
                    view.onFailure(t.toString());
                }

            }
        });

    }

    private void savePersonToDb(List<Person> personList) {
        final List<DbPerson> mPersonList = dbPersonDao.queryBuilder().list();

        if (personList != null) {

            for (Person person : personList) {
                Log.d(TAG, "save person " + person.getName());

                DbPerson foundPerson = findPerson(mPersonList, person.getId());

                if (foundPerson == null) {
                    Log.d(TAG, "insert");
                    DbPerson dbPerson = new DbPerson();
                    dbPerson.setExternalId(person.getId());
                    dbPerson.setName(person.getName());
                    dbPerson.setDeleteMark(person.getDeleteMark());
                    dbPersonDao.insert(dbPerson);
                    Log.d(TAG, " dbPerson getId = " + dbPerson.getId());
                } else {
                    Log.d(TAG, "update");
                    foundPerson.setExternalId(person.getId());
                    foundPerson.setName(person.getName());
                    foundPerson.setDeleteMark(person.getDeleteMark());
                    dbPersonDao.update(foundPerson);
                    Log.d(TAG, " dbPerson getId = " + foundPerson.getId());
                }

            }
        }
    }

    private DbPerson findPerson(List<DbPerson> dbPersonList, String externalId) {

        for (DbPerson person : dbPersonList) {
            if (person.getExternalId().equals(externalId)) {
                return person;
            }
        }

        return null;
    }

    private void getTemplatesFrom1C() {

        Log.d(TAG, "-------------getTemplatesFrom1C------------");
        view.setProgressMessage("Получаем шаблоны...");

        /** Create handle for the RetrofitInstance interface*/
        NetworkDataService service = RetrofitInstance.getRetrofitInstance().create(NetworkDataService.class);

        /** Call the method with parameter in the interface to get the notice data*/
        String Url = PreferenceUtils.getUrl();
        String UserName = PreferenceUtils.getUserName();
        String Password = PreferenceUtils.getPassword();
        Call<List<Template>> call = service.getTemplates(Url + "/GetTemplates/", Credentials.basic(UserName, Password));

        call.enqueue(new Callback<List<Template>>() {
            @Override
            public void onResponse(@NonNull Call<List<Template>> call,
                                   @NonNull Response<List<Template>> response) {
                Log.v(TAG, "success");
                int responseCode = response.code();
                Log.i(TAG, "responseCode = " + response.code());

                if (responseCode == 200) {
                    List<Template> templateList = response.body();

                    Log.d(TAG, "save to DB");
                    saveTemplateToDb(templateList);

                    loadQuestionnaires();
                } else {
                    if (view != null) {
                        view.removeWait();
                        Log.d(TAG, "onError");
                        view.onFailure("Не удалось получить шаблоны.");
                    }
                }

            }

            @Override
            public void onFailure(@NonNull Call<List<Template>> call, @NonNull Throwable t) {
                if (view != null) {
                    view.removeWait();
                    Log.d(TAG, "onError getMessage = " + t.toString());
                    view.onFailure(t.toString());
                }

            }
        });

    }

    private void saveTemplateToDb(List<Template> templateList) {

        final List<DbTemplate> mTemplateList = dbTemplateDao
                .queryBuilder()
                .where(DbTemplateDao.Properties.DeleteMark.eq(false))
                .list();

        if (templateList != null) {

            for (Template template : templateList) {
                Log.d(TAG, "save Template = " + template.getTitle());

                DbTemplate foundTemplate = findTemplate(mTemplateList, template.getId());

                if (foundTemplate == null) {
                    Log.d(TAG, "insert new Template");
                    DbTemplate dbTemplate = new DbTemplate();
                    dbTemplate.setConclusion(template.getConclusion());
                    dbTemplate.setIntroduction(template.getIntroduction());
                    dbTemplate.setExternalId(template.getId());
                    dbTemplate.setTitle(template.getTitle());
                    dbTemplate.setDeleteMark(template.getDeleteMark());
                    dbTemplateDao.insert(dbTemplate);

                    for (Template.Question question : template.getQuestions()) {

                        Log.d(TAG, "insert Question = " + question.getText());

                        DbQuestion dbQuestion = new DbQuestion();
                        dbQuestion.setExternalId(question.getId());
                        dbQuestion.setSimpleId(question.getSimpleId());
                        dbQuestion.setText(question.getText());
                        dbQuestion.setHint(question.getHint());
                        dbQuestion.setIsGroup(question.getIsGroup());
                        dbQuestion.setTemplateId(dbTemplate.getId());

                        dbQuestionDao.insert(dbQuestion);

                    }

                } else {
                    foundTemplate.setConclusion(template.getConclusion());
                    foundTemplate.setIntroduction(template.getIntroduction());
                    foundTemplate.setExternalId(template.getId());
                    foundTemplate.setTitle(template.getTitle());
                    foundTemplate.setDeleteMark(template.getDeleteMark());
                    dbTemplateDao.update(foundTemplate);
                }
            }
        }
    }

    private DbTemplate findTemplate(List<DbTemplate> templateList, String externalId) {

        for (DbTemplate template : templateList) {
            if (template.getExternalId().equals(externalId)) {
                return template;
            }
        }

        return null;
    }

    private void loadQuestionnaires() {

        Log.d(TAG, "-------------loadQuestionnaires------------");
        view.setProgressMessage("Выгружаем анкеты.");

        long LastSyncTime = PreferenceUtils.getLastSyncTime();
        List<DbQuestionnaire> dbQuestionnaireList = dbQuestionnaireDao
                .queryBuilder()
                .where(DbQuestionnaireDao.Properties.DateInMillis.gt(LastSyncTime))
                .list();

        Log.i(TAG, "PreferenceUtils.getLastSyncTime() = " + LastSyncTime);

        if (!dbQuestionnaireList.isEmpty()) {

            Log.i(TAG, "---------!dbQuestionnaireList.isEmpty()--------------");

            final Date currentTime = Calendar.getInstance().getTime();

            //item for request
            Questionnaires questionnaires = prepareQuestionnaires(dbQuestionnaireList);


            /** Create handle for the RetrofitInstance interface*/
            NetworkDataService service = RetrofitInstance.getRetrofitInstance().create(NetworkDataService.class);

            /** Call the method with parameter in the interface to get the notice data*/
            String Url = PreferenceUtils.getUrl();
            String Interviewer = PreferenceUtils.getInterviewer();
            String UserName = PreferenceUtils.getUserName();
            String Password = PreferenceUtils.getPassword();
            Call<ResponseBody> call = service.postCreateQuestionnaire(Url +
                            "/CreateQuestionnaires?Interviewer=" + Interviewer,
                    Credentials.basic(UserName, Password),
                    questionnaires);

            call.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(@NonNull Call<ResponseBody> call,
                                       @NonNull Response<ResponseBody> response) {
                    Log.v(TAG, "success");
                    int responseCode = response.code();
                    Log.i(TAG, "responseCode = " + response.code());

                    if (responseCode == 204) {
                        if (view != null) {
                            view.setProgressMessage("Анкеты выгружены.");
                            PreferenceUtils.saveLastSyncTime(currentTime.getTime());
                            view.removeWait();
                            view.exchangeSuccess();
                            Log.i(TAG, "last sync time = " + PreferenceUtils.getLastSyncTime());
                        }
                    } else {
                        if (view != null) {
                            view.removeWait();
                            Log.d(TAG, "onError");
                            view.onFailure("Не удалось выгрузить анкеты.");
                        }
                    }

                }

                @Override
                public void onFailure(@NonNull Call<ResponseBody> call, @NonNull Throwable t) {
                    if (view != null) {
                        view.removeWait();
                        Log.d(TAG, "onError getMessage = " + t.toString());
                        view.onFailure(t.toString());
                    }
                }
            });


        } else {
            Log.i(TAG, "---------dbQuestionnaireList.isEmpty()--------------");
            if (view != null) {
                view.setProgressMessage("Новых анкет нет.");
                view.removeWait();
                view.exchangeSuccess();
            }

        }

    }

    private Questionnaires prepareQuestionnaires(List<DbQuestionnaire> dbQuestionnaireList) {

        Questionnaires questionnaires = new Questionnaires();

        List<Questionnaire> questionnaireList = new ArrayList<>();

        for (DbQuestionnaire dbQuestionnaire : dbQuestionnaireList) {

            Questionnaire questionnaire = new Questionnaire();

            //заполним основыне реквизиты документа
            questionnaire.setPartnerId(dbQuestionnaire.getRespondent().getExternalId());
            questionnaire.setTemplateId(dbQuestionnaire.getTemplate().getExternalId());
            questionnaire.setIntroduction(dbQuestionnaire.getTemplate().getIntroduction());
            questionnaire.setConclusion(dbQuestionnaire.getTemplate().getConclusion());
            questionnaire.setDate(String.valueOf(dbQuestionnaire.getDateInMillis()));
            questionnaire.setPersonId(dbQuestionnaire.getPerson().getExternalId());
            questionnaire.setComment(dbQuestionnaire.getComment());

            List<DbQuestionForQuestionnaire> dbQuestionForQuestionnaireList = dbQuestionnaire.getAnswerList();

            List<Question> questionList = new ArrayList<>();

            for (DbQuestionForQuestionnaire row : dbQuestionForQuestionnaireList) {

                //заполняем впоросы с ответами
                Question question = new Question();

                question.setId(row.getQuestion().getExternalId());
                question.setSimpleId(row.getQuestion().getSimpleId());
                question.setText(row.getQuestion().getText());
                question.setIsChecked(row.getChecked());
                question.setIsGroup(row.getQuestion().getIsGroup());

                questionList.add(question);

            }

            questionnaire.setQuestions(questionList);
            questionnaireList.add(questionnaire);

        }

        questionnaires.setQuestionnaireList(questionnaireList);

        return questionnaires;
    }
}
