package ru.belugagroup.khrapachevrv.questioningapp.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import ru.belugagroup.khrapachevrv.questioningapp.services.QuestionnairesService;

public class QuestionnairesAlarmReceiver extends BroadcastReceiver {
    public static final int REQUEST_CODE = 1234567;
    public static final String ACTION = "ru.belugagroup.khrapachevrv.questioningapp.alarm";

    // Triggered by the Alarm periodically (starts the service to run task)
    @Override
    public void onReceive(Context context, Intent intent) {

            Intent i = new Intent(context, QuestionnairesService.class);
            //i.putExtra("foo", "bar");
            context.startService(i);

    }
}
