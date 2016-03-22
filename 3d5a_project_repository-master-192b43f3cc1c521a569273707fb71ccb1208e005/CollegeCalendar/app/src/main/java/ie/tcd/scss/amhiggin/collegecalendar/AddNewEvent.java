/*package ie.tcd.scss.amhiggin.collegecalendar;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

public class AddNewEvent extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_new_event);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
    }

}*/
package ie.tcd.scss.amhiggin.collegecalendar;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.io.OutputStreamWriter;

public class AddNewEvent extends ActionBarActivity {

    public final static String MESSAGE_KEY = "ie.tcd.scss.amhiggin.collegecalendar";
    //Strings used to parse data
    public final static String END_OF_STRING = "S_&¦‡¶ÆæÐ♫↕ƒΔå";
    public final static String END_OF_EVENT = "E_&¦‡¶ÆæÐ♫↕ƒΔå";

    String date;
    String dayOfWeek;
    String dayOfMonth;
    String eventFileName;
    //Valid times for input
    String[] validTimeArray ={"0","1","2","3","4","5","6","7","8","9",
            "10","11","12","13","14","15","16","17","18","19","20",
            "21","22","23"};
    String[] MinutesArray ={"00","01","02","03","04","05","06","07","08","09",
            "10","11","12","13","14","15","16","17","18","19","20",
            "21","22","23","24","25","26","27","28","29","30","31","32","33",
            "34","35","36","37","38","39","40","41","42","43","44","45","46","47","48",
            "49","50","51","52","53","54","55","56","57","58","59","60"};
    Boolean viewingEvents;  //Is the user adding an event or a class?

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Get the name of the file the event is to be saved to
        Intent dateIntent = getIntent();    //Sample message: 10-12-15ENDThursdayENDviewingEvents
        date = dateIntent.getStringExtra(ViewTimetable.MESSAGE_KEY);

        //Split the received intent into a date and a day of the week
        //If only a day of the week is received (as in the case of a class timetable),
        //date will be set to that day of the week

        String[] mySplit = date.split("END");
        date = mySplit[0];
        dayOfWeek = mySplit[1];

        if(mySplit[2].equals("viewingEvents")) {
            //User is adding an event
            viewingEvents = true;
            eventFileName = date;
        }
        else {
            //User is adding a class
            viewingEvents = false;
            eventFileName = dayOfWeek;
        }

        //Find the month to save to
        String[] monthSplit = date.split("-");
        dayOfMonth = monthSplit[0];
        date = mySplit[0];
        dayOfWeek = mySplit[1];

        setContentView(R.layout.activity_add_new_event);
        if(!viewingEvents)
        {
            TextView saveEventButton = (TextView) findViewById(R.id.saveEvent_button);
            saveEventButton.setText(getString(R.string.saveClassButton));
        }

        //code for the hour spinner
        ArrayAdapter<String> stringArrayAdapter1 =
                new ArrayAdapter<String>(this,
                        android.R.layout.simple_spinner_dropdown_item,
                        validTimeArray);
        Spinner spinner1 =
                (Spinner) findViewById(R.id.HourSpinner);
        spinner1.setAdapter(stringArrayAdapter1);


        //code for the minute spinner
        ArrayAdapter<String> stringArrayAdapter2 =
                new ArrayAdapter<String>(this,
                        android.R.layout.simple_spinner_dropdown_item,
                        MinutesArray);
        Spinner spinner2 =
                (Spinner) findViewById(R.id.MinutesSpinner);
        spinner2.setAdapter(stringArrayAdapter2);

    }

    //Implements the saveEventButton - saves the event data and ends the AddNewEvent activity
    public void saveEventButton(View view) {

        String event_title, event_location, event_time, event_complete;

        EditText titleBox = (EditText) findViewById(R.id.titleBox);
        EditText locationBox = (EditText) findViewById(R.id.locationBox);

        //Get the input entered by the user
        event_title = titleBox.getText().toString();
        event_location = locationBox.getText().toString();

        event_title = cleanInput(event_title);
        event_location = cleanInput(event_location);

        Spinner spinner1 =(Spinner)findViewById(R.id.HourSpinner);
        Spinner spinner2 =(Spinner)findViewById(R.id.MinutesSpinner);
        event_time = spinner1.getSelectedItem().
                toString()+":"+ spinner2.getSelectedItem().toString();

        if(event_title.equals(""))
            event_title = getString(R.string.no_title_entered);

        event_complete = event_time + END_OF_STRING + event_title + END_OF_STRING + event_location + END_OF_STRING + END_OF_EVENT;

        //Use the entered time to determine which event file to save the event to
        eventFileName = eventFileName + ".txt";

        writeToFile(event_complete, eventFileName);    //Save the entered values to the event file
        if(viewingEvents)
            shortToast( getString(R.string.eventSaved) );
        else
            shortToast( getString(R.string.classSaved) );

        //Return to the ViewTimetable activity
        Intent intent = new Intent(this, ViewTimetable.class);
        String extraString = date + "END" + dayOfWeek;
        if(viewingEvents)
            extraString = extraString + "END" + "viewingEvents";
        else
            extraString = extraString + "END" + "notViewingEvents";

        intent.putExtra(MESSAGE_KEY, extraString); // Tell the Timetable activity the date to open to
        //If this is a class timetable, date will equal the day of the week

        startActivity(intent);
        finish();   //Close the AddNewEvent activity

    }

    //Saves the specified "data" string to the specified file
    private void writeToFile(String data, String fileName) {

        try {
            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(openFileOutput(fileName, Context.MODE_APPEND));
            //Note - changing the context to Context.MODE_APPEND adds to the end of the file, rather than overwriting it
            //     - changing the context to Context.MODE_PRIVATE overwrites the file
            outputStreamWriter.write(data);
            outputStreamWriter.close();
        }
        catch (IOException e) //Won't compile without this method - can put in some debugging messages
        {
        }
    }

    //Implements the cancel button - ends the AddNewEvent activity
    public void cancelButton(View view) {

        //Return to the ViewTimetable activity
        Intent intent = new Intent(this, ViewTimetable.class);
        String extraString = date + "END" + dayOfWeek;
        if(viewingEvents)
            extraString = extraString + "END" + "viewingEvents";
        else
            extraString = extraString + "END" + "notViewingEvents";
        intent.putExtra(MESSAGE_KEY, extraString); // Tell the Timetable activity the date to open to
        startActivity(intent);
        finish();   //Close the AddNewEvent activity, and return to the ViewTimetable activity
    }

    //Display a short Toast message
    public void shortToast(String message)
    {
        Context context = getApplicationContext();
        CharSequence text = message;
        int duration = Toast.LENGTH_SHORT;
        Toast toast = Toast.makeText(context, text, duration);
        toast.show();
    }

    //Remove any END_OF_EVENT and END_OF_STRING strings from this string
    //- used for text inputs, in case the user somehow manages to enter the exact string
    //- this is EXTREMELY unlikely!
    public String cleanInput(String input)
    {

        //Remove the END_OF_EVENT string from the input, if the user has somehow managed to enter it
        String[] inputSplit = input.split(END_OF_EVENT);
        input = "";
        for(int i = 0; i < inputSplit.length; i++)
        {
            input = input + inputSplit[i];
        }

        //Remove the END_OF_STRING string from the input, if the user has somehow managed to enter it
        inputSplit = input.split(END_OF_STRING);
        input = "";
        for(int i = 0; i < inputSplit.length; i++)
        {
            input = input + inputSplit[i];
        }

        return input;

    }

}
