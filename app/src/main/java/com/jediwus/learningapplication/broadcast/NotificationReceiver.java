package com.jediwus.learningapplication.broadcast;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.jediwus.learningapplication.service.NotificationService;

public class NotificationReceiver extends BroadcastReceiver {

    public static final String ACTION_AUDIO = "intent_notification_audio";
    public static final String ACTION_STAR = "intent_notification_star";
    public static final String ACTION_NEXT = "intent_notification_next";

    @Override
    public void onReceive(Context context, Intent intent) {
        switch (intent.getAction()) {
            case ACTION_AUDIO:
                NotificationService.playWordPronunciation();
                break;
            case ACTION_STAR:
                NotificationService.setStarStatus();
                NotificationService.renewTheNotification();
                break;
            case ACTION_NEXT:
                NotificationService.index++;
                if (NotificationService.index == NotificationService.wordList.size()) {
                    NotificationService.index = 0;
                }
                NotificationService.renewTheNotification();
                break;
        }
    }
}
