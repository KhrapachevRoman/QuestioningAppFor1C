package ru.belugagroup.khrapachevrv.questioningapp.networking.synchronous;

import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.Url;
import ru.belugagroup.khrapachevrv.questioningapp.models.network.Partner;
import ru.belugagroup.khrapachevrv.questioningapp.models.network.Person;
import ru.belugagroup.khrapachevrv.questioningapp.models.network.Questionnaires;
import ru.belugagroup.khrapachevrv.questioningapp.models.network.Template;

public interface NetworkDataService {
    @GET()
    Call<ResponseBody> getCheck(@Url String url, @Header("Authorization") String credential);

    @GET()
    Call<List<Partner>> getPartners(@Url String url,@Header("Authorization") String credential);

    @GET()
    Call<List<Person>> getPersons(@Url String url, @Header("Authorization") String credential);

    @GET()
    Call<List<Template>> getTemplates(@Url String url,@Header("Authorization") String credential);

    @POST()
    Call<ResponseBody> postCreateQuestionnaire(@Url String url,@Header("Authorization") String credential,@Body Questionnaires questionnaires);
}
