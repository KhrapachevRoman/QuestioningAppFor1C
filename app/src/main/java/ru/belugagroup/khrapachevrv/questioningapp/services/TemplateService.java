package ru.belugagroup.khrapachevrv.questioningapp.services;

import android.app.IntentService;
import android.content.Intent;
import android.util.Log;

import com.orhanobut.hawk.Hawk;

import java.io.IOException;
import java.util.List;

import okhttp3.Credentials;
import retrofit2.Call;
import ru.belugagroup.khrapachevrv.questioningapp.App;
import ru.belugagroup.khrapachevrv.questioningapp.models.db.DaoSession;
import ru.belugagroup.khrapachevrv.questioningapp.models.db.DbQuestion;
import ru.belugagroup.khrapachevrv.questioningapp.models.db.DbQuestionDao;
import ru.belugagroup.khrapachevrv.questioningapp.models.db.DbTemplate;
import ru.belugagroup.khrapachevrv.questioningapp.models.db.DbTemplateDao;
import ru.belugagroup.khrapachevrv.questioningapp.models.network.Template;
import ru.belugagroup.khrapachevrv.questioningapp.models.prefs.PreferenceUtils;
import ru.belugagroup.khrapachevrv.questioningapp.networking.synchronous.NetworkDataService;
import ru.belugagroup.khrapachevrv.questioningapp.networking.synchronous.RetrofitInstance;

public class TemplateService extends IntentService {
    private final static String TAG = "TemplateService";

    private DbTemplateDao dbTemplateDao;
    private DbQuestionDao dbQuestionDao;
    private List<DbTemplate> mTemplateList;

    public TemplateService() {
        super("TemplateService");

    }

    @Override
    protected void onHandleIntent(Intent intent) {
        // Do the task here
        Log.i(TAG, "Service running");
        // get the note DAO
        DaoSession daoSession = ((App) getApplication()).getDaoSession();
        dbTemplateDao = daoSession.getDbTemplateDao();
        dbQuestionDao = daoSession.getDbQuestionDao();
        mTemplateList = dbTemplateDao.queryBuilder().list();

        Hawk.init(this).build();

        /** Create handle for the RetrofitInstance interface*/
        NetworkDataService service = RetrofitInstance.getRetrofitInstance().create(NetworkDataService.class);

        /** Call the method with parameter in the interface to get the notice data*/
        String Url = PreferenceUtils.getUrl();
        String UserName = PreferenceUtils.getUserName();
        String Password = PreferenceUtils.getPassword();
        Call<List<Template>> call = service.getTemplates(Url+"/GetTemplates/",Credentials.basic(UserName, Password));

        try {

            Log.i(TAG, "---------getTemplates--------------");

            List<Template> templateList =  call.execute().body();

            if (templateList!=null){
                saveToDb(templateList);
            }


        } catch (IOException e) {
            e.printStackTrace();
        }
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
                Log.d(TAG, "update Template");
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
