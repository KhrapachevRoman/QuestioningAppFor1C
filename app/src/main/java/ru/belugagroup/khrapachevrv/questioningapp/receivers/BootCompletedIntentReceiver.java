package ru.belugagroup.khrapachevrv.questioningapp.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class BootCompletedIntentReceiver extends BroadcastReceiver {
    private static final String TAG = "BootIntentReceiver";

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d(TAG, "onReceive - Intent Action: " + intent.getAction());

        if (intent.getAction().equals("android.intent.action.BOOT_COMPLETED")) {
            //запускаем сервисы
            //ScheduleHelper scheduleHelper = new ScheduleHelper();
            //scheduleHelper.scheduleAlarm(context);
        }
    }

}
