package ie.tcd.scss.amhiggin.collegecalendar;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlarmManager;
import android.app.Dialog;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.WakefulBroadcastReceiver;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.TimePicker;

import java.util.Calendar;


/**
 * Created by amhiggin on 04/12/15.
 */
public class Reminder extends FragmentActivity {

    private static int timeHour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY);
    private static int timeMinute = Calendar.getInstance().get(Calendar.MINUTE);
    TextView textView1;
    private static TextView textView2;

    public static TextView getTextView2() {
        return textView2;
    }

    AlarmManager alarmManager;
    private PendingIntent pendingIntent;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_menu);
        textView1 = (TextView) findViewById(R.id.msg1);
        textView1.setText(timeHour + ":" + timeMinute);
        textView2 = (TextView) findViewById(R.id.msg2);

        alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
        Intent myIntent = new Intent(Reminder.this, AlarmReceiver.class);
        pendingIntent = PendingIntent.getBroadcast(Reminder.this, 0, myIntent, 0);

        View.OnClickListener listener1 = new View.OnClickListener() {
            public void onClick(View view) {
                textView2.setText("");
                Bundle bundle = new Bundle();
                bundle.putInt(constants.HOUR, timeHour);
                bundle.putInt(constants.MINUTE, timeMinute);
                _DialogFragment fragment = new _DialogFragment(new MyHandler());
                fragment.setArguments(bundle);
                FragmentManager manager = getSupportFragmentManager();
                FragmentTransaction transaction = manager.beginTransaction();
                transaction.add(fragment, constants.TIME_PICKER);
                transaction.commit();
            }
        };

        Button btn1 = (Button) findViewById(R.id.button1);
        btn1.setOnClickListener(listener1);
        View.OnClickListener listener2 = new View.OnClickListener() {
            public void onClick(View view) {
                textView2.setText("");
                cancelAlarm();
            }
        };
        Button btn2 = (Button) findViewById(R.id.button2);
        btn2.setOnClickListener(listener2);
    }

    class MyHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            Bundle bundle = msg.getData();
            timeHour = bundle.getInt(constants.HOUR);
            timeMinute = bundle.getInt(constants.MINUTE);
            textView1.setText(timeHour + ":" + timeMinute);
            setAlarm();
        }
    }

    @TargetApi(Build.VERSION_CODES.KITKAT)
    private void setAlarm() {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, timeHour);
        calendar.set(Calendar.MINUTE, timeMinute);
        alarmManager.setExact(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
    }

    private void cancelAlarm() {
        if (alarmManager != null) {
            alarmManager.cancel(pendingIntent);
        }
    }

    public abstract class constants {
        public static final String HOUR = "time_hours";
        public static final String MINUTE = "time_minutes";
        public static final String TIME_PICKER = "time_picker";
    }

    public class AlarmReceiver extends WakefulBroadcastReceiver {
        @Override
        public void onReceive(final Context context, Intent intent) {
            Reminder.getTextView2().setText("REMINDER!!!!");
            Uri uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);
            Ringtone ringtone = RingtoneManager.getRingtone(context, uri);
            ringtone.play();
        }
    }


    public class _DialogFragment extends DialogFragment {
        private int timeHour;
        private int timeMinute;
        private Handler handler;

        public _DialogFragment(Handler handler) {
            this.handler = handler;
        }

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            Bundle bundle = getArguments();
            timeHour = bundle.getInt(constants.HOUR);
            timeMinute = bundle.getInt(constants.MINUTE);
            TimePickerDialog.OnTimeSetListener listener = new TimePickerDialog.OnTimeSetListener() {
                public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                    timeHour = hourOfDay;
                    timeMinute = minute;
                    Bundle b = new Bundle();
                    b.putInt(constants.HOUR, timeHour);
                    b.putInt(constants.MINUTE, timeMinute);
                    Message msg = new Message();
                    msg.setData(b);
                    handler.sendMessage(msg);
                }
            };
            return new TimePickerDialog(getActivity(), listener, timeHour, timeMinute, false);
        }
    }
}
