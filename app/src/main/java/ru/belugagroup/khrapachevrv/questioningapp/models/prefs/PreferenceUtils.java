package ru.belugagroup.khrapachevrv.questioningapp.models.prefs;

import android.support.annotation.NonNull;

import com.orhanobut.hawk.Hawk;

public final class PreferenceUtils {
    private static final String DEF_USER_NAME = "AndroidApp";
    private static final String DEF_PASSWORD = "";
    private static final String DEF_INTERVIEWER = "";
    private static final long DEF_LAST_SYNC_TIME = 0;


    private static final String URL_KEY = "URL_KEY";
    private static final String USER_NAME_KEY = "USER_NAME_KEY";
    private static final String PASSWORD_KEY = "PASSWORD_KEY";
    private static final String LAST_SYNC_TIME_KEY = "LAST_SYNC_TIME_KEY";
    private static final String INTERVIEWER_KEY = "INTERVIEWER_KEY";


    public static void saveUrl(@NonNull String url) {
        Hawk.put(URL_KEY, url);
    }

    @NonNull
    public static String getUrl() {
        return Hawk.get(URL_KEY, ru.belugagroup.khrapachevrv.questioningapp.BuildConfig.BASEURL);
    }

    public static void saveUserName(@NonNull String userName) {
        Hawk.put(USER_NAME_KEY, userName);
    }

    @NonNull
    public static String getUserName() {
        return Hawk.get(USER_NAME_KEY, DEF_USER_NAME);
    }


    public static void savePassword(@NonNull String password) {
        Hawk.put(PASSWORD_KEY, password);
    }

    @NonNull
    public static String getPassword() {
        return Hawk.get(PASSWORD_KEY, DEF_PASSWORD);
    }

    public static void saveLastSyncTime(@NonNull long syncTime) {
        Hawk.put(LAST_SYNC_TIME_KEY, syncTime);
    }

    @NonNull
    public static long getLastSyncTime() {
        return Hawk.get(LAST_SYNC_TIME_KEY, DEF_LAST_SYNC_TIME);
    }

    public static void saveInterviewer(@NonNull String interviewer) {
        Hawk.put(INTERVIEWER_KEY, interviewer);
    }

    @NonNull
    public static String getInterviewer() {
        return Hawk.get(INTERVIEWER_KEY, DEF_INTERVIEWER);
    }


}
