package com.bgs.chat.services;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * Created by zhufre on 6/15/2016.
 */
public class ChatAlarmReceiver extends BroadcastReceiver {
    public static int REQUEST_CODE = 89898;
    private static final String ACTION_UPDATE_CONTACT = "in.co.madhur.chatbubblesdemo.action.UPDATE_CONTACT";
    private static final String ACTION_KEEP_CONNECTION = "in.co.madhur.chatbubblesdemo.action.KEEP_CONNECTION";

    @Override
    public void onReceive(Context context, Intent intent) {
        if ( intent != null ) {
            String action = intent.getAction();
            if ( ACTION_UPDATE_CONTACT.equalsIgnoreCase(action)) {
                ChatService.startActionUpdateContact(context);
            } else if ( ACTION_KEEP_CONNECTION.equalsIgnoreCase(action)) {
                ChatService.startActionKeepConnection(context);
            }
        }
    }

    /**
     *
     * @param context
     * @param interval
     */
    public static void startUpdateContactScheduler(Context context, long interval) {
        Intent intent = new Intent(context.getApplicationContext(), ChatAlarmReceiver.class);
        intent.setAction(ACTION_UPDATE_CONTACT);
        schedule(context, intent, interval);
    }


    /**
     *
     * @param context
     * @param interval
     */
    public static void startKeepConnectionScheduler(Context context, long interval) {
        Intent intent = new Intent(context.getApplicationContext(), ChatAlarmReceiver.class);
        intent.setAction(ACTION_KEEP_CONNECTION);
        schedule(context, intent, interval);

    }

    /**
     *
     * @param context
     * @param intent
     * @param interval
     */
    private static void schedule(Context context, Intent intent, long interval) {
        final PendingIntent pendingIntent = PendingIntent.getBroadcast(context, REQUEST_CODE, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        AlarmManager alarm = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
        alarm.setInexactRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(), interval, pendingIntent);
    }


}
