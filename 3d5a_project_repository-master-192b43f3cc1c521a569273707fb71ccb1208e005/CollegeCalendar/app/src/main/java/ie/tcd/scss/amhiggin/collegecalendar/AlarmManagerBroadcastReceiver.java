package ie.tcd.scss.amhiggin.collegecalendar;

import android.app.AlarmManager;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;

/**
 * Created by carandag on 15/12/15.
 */
public class AlarmManagerBroadcastReceiver extends BroadcastReceiver {


    final public static String ONE_TIME = "onetime";

    @Override
    public void onReceive(Context context, Intent intent) {

        createNotification(context, "Time's Up", "10 Seconds has passed",
                "Alert");

    }



    public void setOnetimeTimer(Context context){
        AlarmManager am = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(context, AlarmManagerBroadcastReceiver.class);
        intent.putExtra(ONE_TIME, Boolean.TRUE);
        PendingIntent pi = PendingIntent.getBroadcast(context, 0, intent, 0);
        am.set(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + 10 * 1000, pi);
    }

    public void createNotification(Context context, String msg, String msgText, String msgAlert){

        PendingIntent notificIntent = PendingIntent.getActivity(context, 0,
                new Intent(context, MainMenu.class), 0);


        NotificationCompat.Builder mBuilder = new
                NotificationCompat.Builder(context)
                .setSmallIcon(R.drawable.minilogo)
                .setContentTitle(msg)
                .setTicker(msgAlert)
                .setContentText(msgText);

        mBuilder.setContentIntent(notificIntent);

        mBuilder.setDefaults(NotificationCompat.DEFAULT_SOUND);

        mBuilder.setAutoCancel(true);

        NotificationManager mNotificationManager =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        mNotificationManager.notify(1, mBuilder.build());
    }


}
