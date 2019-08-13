package ru.belugagroup.khrapachevrv.questioningapp.ui.utils;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import java.util.Calendar;

import ru.belugagroup.khrapachevrv.questioningapp.receivers.PartnerAlarmReceiver;
import ru.belugagroup.khrapachevrv.questioningapp.receivers.QuestionnairesAlarmReceiver;
import ru.belugagroup.khrapachevrv.questioningapp.receivers.TemplateAlarmReceiver;

class ScheduleHelper {
    private final static String TAG = "ScheduleHelper";

    public ScheduleHelper() {
    }

    // Setup a recurring alarm
    public void scheduleAlarm(Context context) {

        Log.i(TAG, "scheduleAlarm start");
        //интервал синхронизации 3 часа
        long threeHoursInMillis = 3 * 60 * 60 * 1000;

        //время начала отсчета таймера
        Calendar calendar = Calendar.getInstance();


        //*
        //Инициализация фоновой задачи по партнёрам
        //*

        calendar.setTimeInMillis(System.currentTimeMillis());
        calendar.set(Calendar.HOUR_OF_DAY, 7);
        calendar.set(Calendar.MINUTE, 30);

        // Construct an intent that will execute the AlarmReceiver
        Intent intentPartner = new Intent(context, PartnerAlarmReceiver.class);

        boolean alarmUpPartner = (PendingIntent.getBroadcast(context, PartnerAlarmReceiver.REQUEST_CODE,
                intentPartner, PendingIntent.FLAG_NO_CREATE) != null);

        if (!alarmUpPartner) {
            Log.d(TAG, "alarmUpPartner is do not active");
            // Create a PendingIntent to be triggered when the alarm goes off
            final PendingIntent pIntentPartner = PendingIntent.getBroadcast(context, PartnerAlarmReceiver.REQUEST_CODE,
                    intentPartner, PendingIntent.FLAG_UPDATE_CURRENT);
            // Setup periodic alarm every half hour from this point onwards
            AlarmManager alarmPartner = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
            // First parameter is the type: ELAPSED_REALTIME, ELAPSED_REALTIME_WAKEUP, RTC_WAKEUP
            // Interval can be INTERVAL_FIFTEEN_MINUTES, INTERVAL_HALF_HOUR, INTERVAL_HOUR, INTERVAL_DAY
            alarmPartner.setInexactRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(),
                    AlarmManager.INTERVAL_DAY, pIntentPartner);
        } else {
            Log.d(TAG, "alarmUpPartner is active");
        }

        //*
        //Инициализация фоновой задачи по шаблонам
        //*
        calendar.setTimeInMillis(System.currentTimeMillis());
        calendar.set(Calendar.HOUR_OF_DAY, 8);
        calendar.set(Calendar.MINUTE, 30);
        // Construct an intent that will execute the AlarmReceiver
        Intent intentTemplate = new Intent(context, TemplateAlarmReceiver.class);

        boolean alarmUpTemplate = (PendingIntent.getBroadcast(context, TemplateAlarmReceiver.REQUEST_CODE,
                intentTemplate, PendingIntent.FLAG_NO_CREATE) != null);

        if (!alarmUpTemplate) {
            Log.d(TAG, "alarmUpTemplate is do not active");
            // Create a PendingIntent to be triggered when the alarm goes off
            final PendingIntent pIntentTemplate = PendingIntent.getBroadcast(context, TemplateAlarmReceiver.REQUEST_CODE,
                    intentTemplate, PendingIntent.FLAG_UPDATE_CURRENT);
            AlarmManager alarmTemplate = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
            // First parameter is the type: ELAPSED_REALTIME, ELAPSED_REALTIME_WAKEUP, RTC_WAKEUP
            // Interval can be INTERVAL_FIFTEEN_MINUTES, INTERVAL_HALF_HOUR, INTERVAL_HOUR, INTERVAL_DAY
            alarmTemplate.setInexactRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(),
                    AlarmManager.INTERVAL_FIFTEEN_MINUTES, pIntentTemplate);
        } else {
            Log.d(TAG, "alarmUpTemplate is active");
        }

        //*
        //Инициализация фоновой задачи по анкетам
        //*
        calendar.setTimeInMillis(System.currentTimeMillis());
        calendar.set(Calendar.HOUR_OF_DAY, 9);
        calendar.set(Calendar.MINUTE, 0);
        // Construct an intent that will execute the AlarmReceiver
        Intent intentQuestionnaire = new Intent(context, QuestionnairesAlarmReceiver.class);

        boolean alarmUpQuestionnaire = (PendingIntent.getBroadcast(context, QuestionnairesAlarmReceiver.REQUEST_CODE,
                intentQuestionnaire, PendingIntent.FLAG_NO_CREATE) != null);

        if (!alarmUpQuestionnaire) {
            Log.d(TAG, "alarmUpQuestionnaire is do not active");

            // Create a PendingIntent to be triggered when the alarm goes off
            final PendingIntent pIntentQuestionnaire = PendingIntent.getBroadcast(context, QuestionnairesAlarmReceiver.REQUEST_CODE,
                    intentQuestionnaire, PendingIntent.FLAG_UPDATE_CURRENT);
            AlarmManager alarmQuestionnaire = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
            // First parameter is the type: ELAPSED_REALTIME, ELAPSED_REALTIME_WAKEUP, RTC_WAKEUP
            // Interval can be INTERVAL_FIFTEEN_MINUTES, INTERVAL_HALF_HOUR, INTERVAL_HOUR, INTERVAL_DAY
            alarmQuestionnaire.setInexactRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(),
                    AlarmManager.INTERVAL_FIFTEEN_MINUTES, pIntentQuestionnaire);
        } else {
            Log.d(TAG, "alarmUpQuestionnaire is active");
        }

    }
}
