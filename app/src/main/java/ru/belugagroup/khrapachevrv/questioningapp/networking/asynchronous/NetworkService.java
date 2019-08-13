package ru.belugagroup.khrapachevrv.questioningapp.networking.asynchronous;

import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.Url;
import ru.belugagroup.khrapachevrv.questioningapp.models.network.Partner;
import ru.belugagroup.khrapachevrv.questioningapp.models.network.Questionnaires;
import ru.belugagroup.khrapachevrv.questioningapp.models.network.Template;
import rx.Observable;

public interface NetworkService {
    @GET()
    Observable<ResponseBody> getCheck(@Url String url,@Header("Authorization") String credential);

    @GET()
    Observable<List<Partner>> getPartners(@Url String url,@Header("Authorization") String credential);

    @GET()
    Observable<List<Template>> getTemplates(@Url String url,@Header("Authorization") String credential);

    @POST()
    Observable<ResponseBody> postCreateQuestionnaire(@Url String url,@Header("Authorization") String credential,@Body Questionnaires questionnaires);
}
