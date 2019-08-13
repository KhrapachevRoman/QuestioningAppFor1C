package ru.belugagroup.khrapachevrv.questioningapp.networking.asynchronous;

import android.text.TextUtils;
import android.util.Log;

import com.google.gson.Gson;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import retrofit2.Response;
import retrofit2.adapter.rxjava.HttpException;

import static java.net.HttpURLConnection.HTTP_UNAUTHORIZED;

/**
 * Created by hrapachev on 13.04.2018.
 */


public class NetworkError  extends Throwable {
    private static final String DEFAULT_ERROR_MESSAGE = "Что-то пошло не так! Попробуйте ещё раз.";
    private static final String NETWORK_ERROR_MESSAGE = "Нет соединения с сетью!";
    private static final String ERROR_MESSAGE_HEADER = "Error-Message";
    private final Throwable error;

    public NetworkError(Throwable e) {
        super(e);
        this.error = e;
    }

    public String getMessage() {
        return error.getMessage();
    }

    public boolean isAuthFailure() {
        return error instanceof HttpException &&
                ((HttpException) error).code() == HTTP_UNAUTHORIZED;
    }

    public boolean isResponseNull() {
        return error instanceof HttpException && ((HttpException) error).response() == null;
    }

    public String getAppErrorMessage() {
        if (this.error instanceof IOException) return NETWORK_ERROR_MESSAGE;
        if (!(this.error instanceof HttpException)) return DEFAULT_ERROR_MESSAGE;
        Response<?> response = ((HttpException) this.error).response();
        if (response != null) {
            String status = getJsonStringFromResponse(response);
            Log.d ("NetworkError","response status = " + status);
            if (!TextUtils.isEmpty(status)) return status;

            Map<String, List<String>> headers = response.headers().toMultimap();
            if (headers.containsKey(ERROR_MESSAGE_HEADER))
                return headers.get(ERROR_MESSAGE_HEADER).get(0);
        }

        return DEFAULT_ERROR_MESSAGE;
    }

    private String getJsonStringFromResponse(final Response<?> response) {
        try {
            String jsonString = response.errorBody().string();
            Log.d ("NetworkError","response jsonString = " + jsonString);
            Response errorResponse = new Gson().fromJson(jsonString, Response.class);
            return errorResponse.message();
        } catch (Exception e) {
            return null;
        }
    }

    public Throwable getError() {
        return error;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        NetworkError that = (NetworkError) o;

        return error != null ? error.equals(that.error) : that.error == null;

    }

    @Override
    public int hashCode() {
        return error != null ? error.hashCode() : 0;
    }
}
