package ie.tcd.scss.amhiggin.collegecalendar;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;

import java.util.Calendar;

public class MainMenu extends ActionBarActivity {

    public final static String MESSAGE_KEY = "ie.tcd.scss.amhiggin.collegecalendar";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_menu);


        Button button = (Button) findViewById(R.id.button1);

        button.setOnClickListener(new View.OnClickListener() {

            @Override

            public void onClick(View v) {

                goToButton1Activity();

            }

        });

        Button button3 = (Button) findViewById(R.id.button3);

        button3.setOnClickListener(new View.OnClickListener() {

            @Override

            public void onClick(View v) {

                goToButton3Activity();

            }

        });

        Button toDoButton = (Button) findViewById(R.id.toDoButton);
        toDoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToToDoListActivity();
            }
        });
    }

    private void goToButton1Activity() {

        Intent intent = new Intent(this, DisplayCalendar.class);

        startActivity(intent);

    }

    private void goToButton3Activity() {

        Intent intent = new Intent(this, Button3Activity.class);

        startActivity(intent);

    }

    private void goToToDoListActivity()
    {
        Intent intent = new Intent(this, ToDoList.class);
        startActivity(intent);
    }

    //Implements the button that bring the user to the timetable for today's date
    public void viewTodayTimetable(View view)  {

        //Determine today's date
        Calendar selected = Calendar.getInstance();
        int year = selected.get(Calendar.YEAR);
        int month = selected.get(Calendar.MONTH);
        int dayOfMonth = selected.get(Calendar.DAY_OF_MONTH);

        //Determine the day of the week
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

        //Concatenate the date and day of week strings, and pass them to the timetable activity
        String finalExtra = complete_date + "END" + dayOfWeekString + "END" + "viewingEvents";    //e.g. 24-11-2015ENDMondayENDviewingEvents (event view is default)
        Intent intent = new Intent(this, ViewTimetable.class);
        intent.putExtra(MESSAGE_KEY, finalExtra);
        startActivity(intent);
    }

    //Implements the button that bring the user to the timetable for today's date
    public void viewNotes(View view) {
        Intent intent = new Intent(this, ViewNotes.class);
        startActivity(intent);
    }
}
