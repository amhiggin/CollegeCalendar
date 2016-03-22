package ie.tcd.scss.amhiggin.collegecalendar;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.View;
import android.widget.CalendarView;
import android.widget.CalendarView.OnDateChangeListener;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Toast;
import android.app.Activity;

import java.util.Calendar;


/**
 * Created by amhiggin on 17/11/15.
 */
public class DisplayCalendar extends ActionBarActivity {

    public final static String MESSAGE_KEY = "ie.tcd.scss.amhiggin.collegecalendar";

    CalendarView cal;

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    @Override
    protected void onCreate(Bundle savedInstanceState) //Opens when this activity is created
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_calendar);
        cal = (CalendarView) findViewById(R.id.calendarView);

        //Intent must be declared as final for it to be accessed inside the dateChangeListener
        final Intent intent = new Intent(this, ViewTimetable.class);
        cal.setOnDateChangeListener(new OnDateChangeListener() {

            public void onSelectedDayChange(CalendarView _view, int year, int month, int dayOfMonth) {

                //Determine the day of the week for the selected date
                Calendar selected = Calendar.getInstance();
                selected.set(year, month, dayOfMonth);
                int dayOfWeek = selected.get(selected.DAY_OF_WEEK);
                String dayOfWeekString = String.valueOf(dayOfWeek);

                //Set the appropriate string for the day of the week
                switch(dayOfWeekString) {
                    case "1": {
                        dayOfWeekString = getString(R.string.Sunday);
                        break;
                    }
                    case "2": {
                        dayOfWeekString = getString(R.string.Monday);
                        break;
                    }
                    case "3": {
                        dayOfWeekString = getString(R.string.Tuesday);
                        break;
                    }
                    case "4": {
                        dayOfWeekString = getString(R.string.Wednesday);
                        break;
                    }
                    case "5": {
                        dayOfWeekString = getString(R.string.Thursday);
                        break;
                    }
                    case "6": {
                        dayOfWeekString = getString(R.string.Friday);
                        break;
                    }
                    case "7": {
                        dayOfWeekString = getString(R.string.Saturday);
                        break;
                    }
                }

                //Set the date as a string
                month++;    //The months in the Android calendar range from 0-11, rather than 1-12
                String complete_date = Integer.toString(dayOfMonth) + "-" + Integer.toString(month) + "-" + Integer.toString(year);

                //Concatenate the date and day of week strings, and pass them to the ViewTimetable activity
                String finalExtra = complete_date + "END" + dayOfWeekString + "END" + "viewingEvents";    //e.g. 24-11-2015ENDMondayENDviewingEvents (timetable opens to events by default)
                intent.putExtra(MESSAGE_KEY, finalExtra);
                startActivity(intent);
            }
        });
    }

}