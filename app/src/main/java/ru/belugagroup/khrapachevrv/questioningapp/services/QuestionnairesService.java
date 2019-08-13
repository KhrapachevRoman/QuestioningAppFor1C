package ru.belugagroup.khrapachevrv.questioningapp.services;

import android.app.IntentService;
import android.content.Intent;
import android.util.Log;

import com.orhanobut.hawk.Hawk;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import okhttp3.Credentials;
import okhttp3.ResponseBody;
import retrofit2.Call;
import ru.belugagroup.khrapachevrv.questioningapp.App;
import ru.belugagroup.khrapachevrv.questioningapp.models.db.DaoSession;
import ru.belugagroup.khrapachevrv.questioningapp.models.db.DbQuestionForQuestionnaire;
import ru.belugagroup.khrapachevrv.questioningapp.models.db.DbQuestionnaire;
import ru.belugagroup.khrapachevrv.questioningapp.models.db.DbQuestionnaireDao;
import ru.belugagroup.khrapachevrv.questioningapp.models.network.Question;
import ru.belugagroup.khrapachevrv.questioningapp.models.network.Questionnaire;
import ru.belugagroup.khrapachevrv.questioningapp.models.network.Questionnaires;
import ru.belugagroup.khrapachevrv.questioningapp.models.prefs.PreferenceUtils;
import ru.belugagroup.khrapachevrv.questioningapp.networking.synchronous.NetworkDataService;
import ru.belugagroup.khrapachevrv.questioningapp.networking.synchronous.RetrofitInstance;


public class QuestionnairesService extends IntentService {
    private final static String TAG = "QuestionnairesService";


    public QuestionnairesService() {
        super("QuestionnairesService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        // Do the task here
        Log.i(TAG, "Service running");

        Hawk.init(this).build();
        // get the note DAO
        DaoSession daoSession = ((App) getApplication()).getDaoSession();
        DbQuestionnaireDao dbQuestionnaireDao = daoSession.getDbQuestionnaireDao();

        long LastSyncTime = PreferenceUtils.getLastSyncTime();
        List<DbQuestionnaire> dbQuestionnaireList = dbQuestionnaireDao
                .queryBuilder()
                .where(DbQuestionnaireDao.Properties.DateInMillis.gt(LastSyncTime))
                .list();

        Log.i(TAG, "PreferenceUtils.getLastSyncTime() = "+ LastSyncTime);

        if (!dbQuestionnaireList.isEmpty()) {

            Log.i(TAG, "---------!dbQuestionnaireList.isEmpty()--------------");

            Date currentTime = Calendar.getInstance().getTime();

            //item for request
            Questionnaires questionnaires = new Questionnaires();

            List<Questionnaire> questionnaireList = new ArrayList<>();

            for (DbQuestionnaire dbQuestionnaire : dbQuestionnaireList) {

                Questionnaire questionnaire = new Questionnaire();

                //заполним основыне реквизиты документа
                questionnaire.setPartnerId(dbQuestionnaire.getRespondent().getExternalId());
                questionnaire.setTemplateId(dbQuestionnaire.getTemplate().getExternalId());
                questionnaire.setIntroduction(dbQuestionnaire.getTemplate().getIntroduction());
                questionnaire.setConclusion(dbQuestionnaire.getTemplate().getConclusion());

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

            /** Create handle for the RetrofitInstance interface*/
            NetworkDataService service = RetrofitInstance.getRetrofitInstance().create(NetworkDataService.class);

            /** Call the method with parameter in the interface to get the notice data*/
            String Url = PreferenceUtils.getUrl();
            String Interviewer = PreferenceUtils.getInterviewer();
            String UserName = PreferenceUtils.getUserName();
            String Password = PreferenceUtils.getPassword();
            Call<ResponseBody> call = service.postCreateQuestionnaire(Url +
                    "/CreateQuestionnaires?Interviewer="+Interviewer,
                    Credentials.basic(UserName,Password),
                    questionnaires);

            try {

                Log.i(TAG, "try call ");

                int responseCode = call.execute().code();

                if (responseCode ==204) {

                    Log.i(TAG, "responseCode = " + responseCode);

                    PreferenceUtils.saveLastSyncTime(currentTime.getTime());
                    Log.i(TAG, "last sync time = " + PreferenceUtils.getLastSyncTime());

                }else{
                    Log.e(TAG, "responseCode = " + responseCode);
                }

            } catch (IOException e) {
                e.printStackTrace();
                Log.e(TAG, " Error = " +  e.toString());
            }

        }else{
            Log.i(TAG, "---------dbQuestionnaireList.isEmpty()--------------");
        }
    }

}
