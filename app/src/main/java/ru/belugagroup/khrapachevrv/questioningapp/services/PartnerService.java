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
import ru.belugagroup.khrapachevrv.questioningapp.models.db.DbRespondent;
import ru.belugagroup.khrapachevrv.questioningapp.models.db.DbRespondentDao;
import ru.belugagroup.khrapachevrv.questioningapp.models.network.Partner;
import ru.belugagroup.khrapachevrv.questioningapp.models.prefs.PreferenceUtils;
import ru.belugagroup.khrapachevrv.questioningapp.networking.synchronous.NetworkDataService;
import ru.belugagroup.khrapachevrv.questioningapp.networking.synchronous.RetrofitInstance;


public class PartnerService extends IntentService {
    private final static String TAG = "PartnerService";


    public PartnerService() {
        super("PartnerService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        // Do the task here
        Log.i(TAG, "Service running");
        // get the note DAO
        DaoSession daoSession = ((App) getApplication()).getDaoSession();
        DbRespondentDao dbRespondentDao = daoSession.getDbRespondentDao();
        List<DbRespondent> mPartnerList = dbRespondentDao.queryBuilder().list();

        Hawk.init(this).build();

        /** Create handle for the RetrofitInstance interface*/
        NetworkDataService service = RetrofitInstance.getRetrofitInstance().create(NetworkDataService.class);

        /** Call the method with parameter in the interface to get the notice data*/
        String Url = PreferenceUtils.getUrl();
        String Interviewer = PreferenceUtils.getInterviewer();
        String UserName = PreferenceUtils.getUserName();
        String Password = PreferenceUtils.getPassword();
        Log.i(TAG, Url+"GetPartners?Interviewer="+Interviewer+Credentials.basic(UserName, Password));
        Call<List<Partner>> call = service.getPartners(Url+"/GetPartners?Interviewer="+Interviewer,Credentials.basic(UserName, Password));

        try {

            Log.i(TAG, "---------getPartners--------------");

            List<Partner>  partnerList =  call.execute().body();

            if (partnerList!=null){

                for (Partner partner : partnerList) {
                    Log.d(TAG, "save partner "+partner.getName());

                    DbRespondent foundRespondent = findRespondent(partner.getId(),mPartnerList);

                    if (foundRespondent==null){
                        Log.d(TAG, "insert");
                        DbRespondent dbRespondent = new DbRespondent();
                        dbRespondent.setExternalId(partner.getId());
                        dbRespondent.setName(partner.getName());
                        dbRespondent.setDeleteMark(partner.getDeleteMark());
                        dbRespondentDao.insert(dbRespondent);
                        Log.d(TAG, " dbRespondent getId = "+ dbRespondent.getId());
                    }else{
                        Log.d(TAG, "update");
                        foundRespondent.setExternalId(partner.getId());
                        foundRespondent.setName(partner.getName());
                        foundRespondent.setDeleteMark(partner.getDeleteMark());
                        dbRespondentDao.update(foundRespondent);
                        Log.d(TAG, " dbRespondent getId = "+ foundRespondent.getId());
                    }

                }
            }


        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private DbRespondent findRespondent(String externalId,List<DbRespondent> dbRespondentList){

        for(DbRespondent respondent : dbRespondentList) {
            if(respondent.getExternalId().equals(externalId)) {
                return respondent;
            }
        }

        return null;
    }
}
