package ru.belugagroup.khrapachevrv.questioningapp;

import android.app.Application;


import org.greenrobot.greendao.database.Database;

import ru.belugagroup.khrapachevrv.questioningapp.models.db.DaoMaster;
import ru.belugagroup.khrapachevrv.questioningapp.models.db.DaoSession;

public class App extends Application  {

    private DaoSession daoSession;

    @Override
    public void onCreate() {
        super.onCreate();

        // regular SQLite database
        DaoMaster.DevOpenHelper helper = new DaoMaster.DevOpenHelper(this, "questioning-db");
        Database db = helper.getWritableDb();

        daoSession = new DaoMaster(db).newSession();
    }

    public DaoSession getDaoSession() {
        return daoSession;
    }
}
