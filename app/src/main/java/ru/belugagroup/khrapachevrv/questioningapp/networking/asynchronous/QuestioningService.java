package ru.belugagroup.khrapachevrv.questioningapp.networking.asynchronous;

import java.util.List;

import okhttp3.ResponseBody;
import ru.belugagroup.khrapachevrv.questioningapp.models.network.Partner;
import ru.belugagroup.khrapachevrv.questioningapp.models.network.Questionnaires;
import ru.belugagroup.khrapachevrv.questioningapp.models.network.Template;
import rx.Observable;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

public class QuestioningService {

    private final NetworkService networkService;

    public QuestioningService(NetworkService networkService) {this.networkService = networkService;}

    public Subscription getPartnersList(final QuestioningService.PartnersListDataCallback callback, String url,String credential) {

        return networkService.getPartners(url,credential)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .onErrorResumeNext(new Func1<Throwable, Observable<? extends List<Partner> >>() {
                    @Override
                    public Observable<? extends List<Partner> > call(Throwable throwable) {
                        return Observable.error(throwable);
                    }
                })
                .subscribe(new Subscriber<List<Partner> >() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        callback.onError(new NetworkError(e));

                    }

                    @Override
                    public void onNext(List<Partner>  partnerList) {
                        callback.onSuccess(partnerList);

                    }
                });
    }

    public interface PartnersListDataCallback {
        void onSuccess(List<Partner> partnerList);

        void onError(NetworkError networkError);
    }

    public Subscription getTemplatesList(final QuestioningService.TemplatesListDataCallback callback,String url,String credential) {

        return networkService.getTemplates(url,credential)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .onErrorResumeNext(new Func1<Throwable, Observable<? extends List<Template> >>() {
                    @Override
                    public Observable<? extends List<Template> > call(Throwable throwable) {
                        return Observable.error(throwable);
                    }
                })
                .subscribe(new Subscriber<List<Template> >() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        callback.onError(new NetworkError(e));

                    }

                    @Override
                    public void onNext(List<Template>  templateList) {
                        callback.onSuccess(templateList);

                    }
                });
    }

    public interface TemplatesListDataCallback {
        void onSuccess(List<Template> templateList);

        void onError(NetworkError networkError);
    }


    public Subscription postCreateQuestionnaire(final QuestioningService.ResponseBodyDataCallback callback, String url,String credential, Questionnaires questionnaires) {

        return networkService.postCreateQuestionnaire(url,credential,questionnaires)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .onErrorResumeNext(new Func1<Throwable, Observable<? extends ResponseBody >>() {
                    @Override
                    public Observable<? extends ResponseBody > call(Throwable throwable) {
                        return Observable.error(throwable);
                    }
                })
                .subscribe(new Subscriber<ResponseBody >() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        callback.onError(new NetworkError(e));

                    }

                    @Override
                    public void onNext(ResponseBody responseBody) {
                        callback.onSuccess(responseBody);

                    }
                });
    }

    interface ResponseBodyDataCallback {
        void onSuccess(ResponseBody responseBody);

        void onError(NetworkError networkError);
    }

    public Subscription getCheck(final QuestioningService.ResponseBodyDataCallback callback, String url,String credential) {

        return networkService.getCheck(url,credential)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .unsubscribeOn(Schedulers.io())
                .onErrorResumeNext(new Func1<Throwable, Observable<? extends ResponseBody >>() {
                    @Override
                    public Observable<? extends ResponseBody > call(Throwable throwable) {
                        return Observable.error(throwable);
                    }
                })
                .subscribe(new Subscriber<ResponseBody >() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        callback.onError(new NetworkError(e));

                    }

                    @Override
                    public void onNext(ResponseBody responseBody) {
                        callback.onSuccess(responseBody);

                    }


                });
    }


}
