package ie.tcd.scss.amhiggin.collegecalendar;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;

public class editEvent extends AppCompatActivity {

    public final static String MESSAGE_KEY = "ie.tcd.scss.amhiggin.collegecalendar";
    //Strings used to parse data
    public final static String END_OF_STRING = "S_&¦‡¶ÆæÐ♫↕ƒΔå";
    public final static String END_OF_EVENT = "E_&¦‡¶ÆæÐ♫↕ƒΔå";
    public final static String END_OF_IDENTIFIER = "I_&¦‡¶ÆæÐ♫↕ƒΔå";

    String date;
    String dayOfWeek;
    String eventFileName;
    String[] validTimeArray = {"0","1","2","3","4","5","6","7","8","9",
            "10","11","12","13","14","15","16","17","18","19","20",
            "21","22","23"};
    String[] MinutesArray ={"00","01","02","03","04","05","06","07","08","09",
            "10","11","12","13","14","15","16","17","18","19","20",
            "21","22","23","24","25","26","27","28","29","30","31","32","33",
            "34","35","36","37","38","39","40","41","42","43","44","45","46","47","48",
            "49","50","51","52","53","54","55","56","57","58","59","60"};
    Boolean viewingEvents;
    ArrayList<String> eventArray = new ArrayList<String>();
    ArrayList<Integer> orderArray = new ArrayList<>();
    String oldEventText;
    Boolean monthView = false;
    int spinner1Position, spinner2Position;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_event);

        //Get the name of the file the event is to be saved to
        Intent dateIntent = getIntent();
        date = dateIntent.getStringExtra(ViewTimetable.MESSAGE_KEY);
        //Sample message: 10-12-2015ENDThursdayENDviewingEventsEND_OF_IDENTIFIER9:00END_OF_STRINGTitleEND_OF_STRINGLocation
        // corresponds to 10-12-2015 Thursday 9:00 Title Location

        //Split the intent message into the date information, and the event information
        String[] myInitialSplit = date.split(END_OF_IDENTIFIER);
        //Splits into 10-12-2015ENDThursdayENDviewingEvents
        //and 9:00END_OF_STRINGTitleEND_OF_STRINGLocation

        //Split up the date and event information
        String[] dateInfo = myInitialSplit[0].split("END");
        String[] eventInfo = myInitialSplit[1].split(END_OF_STRING);

        date = dateInfo[0];
        dayOfWeek = dateInfo[1];

        if(dateInfo[2].equals("viewingEvents")) {
            viewingEvents = true;
            eventFileName = date;
        }
        else {
            viewingEvents = false;
            eventFileName = dayOfWeek;
            if(dateInfo[2].equals("monthView")) //If this edit came from the viewMonth activity
            {
                monthView = true;
                eventFileName = date;
            }

        }

        String title, location;
        String[] myTimeSplit = eventInfo[0].split(":"); //Remove the colon from the time, e.g. 9:00 --> 9 00
        spinner1Position = Integer.valueOf(myTimeSplit[0]);
        spinner2Position = Integer.valueOf(myTimeSplit[1]);

        title = eventInfo[1];
        if(eventInfo.length == 3)   //If a location was entered
            location = eventInfo[2];
        else
            location = "";

        setContentView(R.layout.activity_edit_event);

        //code for the spinner to be placed in the time box
        ArrayAdapter<String> stringArrayAdapter1 =
                new ArrayAdapter<String>(this,
                        android.R.layout.simple_spinner_dropdown_item,
                        validTimeArray);
        Spinner spinner1 =
                (Spinner) findViewById(R.id.HourSpinner);
        spinner1.setAdapter(stringArrayAdapter1);

        //code for the spinner to be placed in the time box
        ArrayAdapter<String> stringArrayAdapter2 =
                new ArrayAdapter<String>(this,
                        android.R.layout.simple_spinner_dropdown_item,
                        MinutesArray);
        Spinner spinner2 =
                (Spinner) findViewById(R.id.MinutesSpinner);
        spinner2.setAdapter(stringArrayAdapter2);

        //Set the spinner times to match the save data of the event being edited
        spinner1.setSelection(spinner1Position);
        spinner2.setSelection(spinner2Position);

        EditText titleBox = (EditText) findViewById(R.id.titleBox);
        if(title.equals(getString(R.string.no_title_entered)))
        {
            //Do nothing
        }
        else
            titleBox.setText(title);

        EditText locationBox = (EditText) findViewById(R.id.locationBox);
        if(eventInfo.length == 3)   //If a location was entered
        {
            locationBox.setText(location);
        }

        //Make a record of the event being edited - use this later to determine which event in the array to replace with edited data
        oldEventText = myTimeSplit[0] + ":" + myTimeSplit[1] + END_OF_STRING + title + END_OF_STRING + location + END_OF_STRING + END_OF_EVENT;

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

    //Implements the cancel button - ends the editEvent activity
    public void cancelButton(View view) {

        Intent intent;
        if(monthView)
            intent = new Intent(this, viewMonth.class);
        else
            intent = new Intent(this, ViewTimetable.class);
        String extraString = date + "END" + dayOfWeek;
        if(viewingEvents)
            extraString = extraString + "END" + "viewingEvents";
        else
            extraString = extraString + "END" + "notViewingEvents";
        intent.putExtra(MESSAGE_KEY, extraString); // Tell the Timetable activity the date to open to
        startActivity(intent);
        finish();   //Close the AddNewEvent activity, and return to the ViewTimetable activity
    }

    //Implements the saveEventButton - saves the event data and ends the editEvent activity
    public void saveEventButton(View view) {

        String event_title, event_location, event_time, event_complete;

        EditText titleBox = (EditText) findViewById(R.id.titleBox);
        EditText locationBox = (EditText) findViewById(R.id.locationBox);

        //Get the input entered by the user
        event_title = titleBox.getText().toString();
        event_location = locationBox.getText().toString();

        event_title = cleanInput_specialIdentifier(event_title);
        event_location = cleanInput_specialIdentifier(event_location);

        Spinner spinner1 =(Spinner)findViewById(R.id.HourSpinner);
        Spinner spinner2 =(Spinner)findViewById(R.id.MinutesSpinner);
        event_time = spinner1.getSelectedItem().
                toString()+":"+ spinner2.getSelectedItem().toString();

        if(event_title.equals(""))
            event_title = getString(R.string.no_title_entered);

        event_complete = event_time + END_OF_STRING + event_title + END_OF_STRING + event_location + END_OF_STRING + END_OF_EVENT;

        //Use the entered time to determine which event file to save the event to
        //eventFileName = eventFileName + "_" + String.valueOf(slot) + ".txt";
        eventFileName = eventFileName + ".txt";

        //Generate the array of events already on file

        //viewingEvents Boolean value has already been set (in the onCreate() method)

        if(viewingEvents || monthView)
            populateTimetable_noLineBreaks(date);
        else
            populateTimetable_noLineBreaks(dayOfWeek);


        //Array will now have been generated
        //Remove the old version of the event
        for(int i = 0; i < eventArray.size(); i++)  //Loop through the array
        {
            if(oldEventText.equals(eventArray.get(i))) {
                eventArray.remove(i);
                orderArray.remove(i);
            }
        }

        //Now add the new version of the event
        //Place time in orderArray - use the contents of orderArray to order the listView events
        String[] myTimeSplit = event_time.split(":");
        Integer orderInput = Integer.valueOf(myTimeSplit[0]) + Integer.valueOf(myTimeSplit[1]);
        Boolean added = false;
        int j = 0;
        while(!added && j < orderArray.size() )
        {
            if(orderInput < orderArray.get(j)) {
                orderArray.add(j, orderInput);  //Add the time to the orderArray, at a certain position
                added = true;

                eventArray.add(j, event_complete);  //Add the event at the same postion in the eventArray
                event_complete = "";
            }
            j++;
        }

        if(!added)  //The list was blank - this was the first entry
        {
            orderArray.add(orderInput);

            eventArray.add(event_complete);
            event_complete = "";
        }

        //Now, loop through the array, and take all the text from it
        String allArrayInput = "";

        for(int i = 0; i < eventArray.size(); i++)
        {
            allArrayInput = allArrayInput + eventArray.get(i);
        }

        writeToFile_Overwrite(allArrayInput);    //Save the entered values
        shortToast( getString(R.string.changesSaved) );

        Intent intent;
        if(monthView)
            intent = new Intent(this, viewMonth.class);
        else
            intent = new Intent(this, ViewTimetable.class);
        String extraString = date + "END" + dayOfWeek;
        if(viewingEvents)
            extraString = extraString + "END" + "viewingEvents";
        else
            extraString = extraString + "END" + "notViewingEvents";
        intent.putExtra(MESSAGE_KEY, extraString); // Tell the Timetable activity the date to open to
        startActivity(intent);
        finish();   //Close the editEvent activity, and return to the ViewTimetable activity

    }

    //Populates the contents of the timetable to match saved values (if any exist) for the selected day
    public void populateTimetable_noLineBreaks(final String selected_date)
    {

        String eventFileName;

        eventFileName = selected_date + ".txt";

        String myString = readFromFile(eventFileName);  //Search the internal storage for data corresponding to eventFileName.txt
        String arrayInput = "";

        String[] myOtherSplit = myString.split(END_OF_EVENT);
        //e.g. split into multiple timeEND_OF_STRINGtitleEND_OF_STRINGlocation strings

        for(int i = 0; i < myOtherSplit.length; i++)
        {
            String[] mySplit = myOtherSplit[i].split(END_OF_STRING);
            //split into time title location

            arrayInput = myOtherSplit[i] + END_OF_EVENT;   //e.g. arrayInput = timeEND_OF_STRINGtitleEND_OF_STRINGlocationEND_OF_EVENT

            //Find the saved time
            Integer orderInput;
            if(mySplit[0].equals(""))   //Blank file - no time to take from the split
                orderInput = 0;
            else {
                String[] myTimeSplit = mySplit[0].split(":");
                orderInput = Integer.valueOf(myTimeSplit[0] + myTimeSplit[1]);}
            Boolean added = false;
            int j = 0;

            //Place time in orderArray - use the contents of orderArray to order the listView events
            while(!added && j < orderArray.size() )
            {
                if(orderInput < orderArray.get(j)) {
                    orderArray.add(j, orderInput);  //Add the time to the orderArray, at a certain position
                    added = true;

                    eventArray.add(j, arrayInput);  //Add the event at the same position in the eventArray
                    arrayInput = "";
                }
                j++;
            }

            if(!added)  //The list was blank - this was the first entry
            {
                orderArray.add(orderInput);
                eventArray.add(arrayInput);
                arrayInput = "";
            }
        }

    }

    //Reads values from the saved file. Used in populateTimetable_noLineBreaks()
    private String readFromFile(String dateFileName) {

        String inputFromFile = "";

        try {
            InputStream myInputStream = openFileInput(dateFileName);
            //This will not work with arbitrary file names - the chosen file name must be referenced elsewhere
            //within this activity

            if (myInputStream != null)
            {
                InputStreamReader myInputStreamReader = new InputStreamReader(myInputStream);
                BufferedReader myBufferedReader = new BufferedReader(myInputStreamReader);
                StringBuilder myStringBuilder = new StringBuilder();
                String myInputString = myBufferedReader.readLine();

                while(myInputString != null)
                {
                    myStringBuilder.append(myInputString);  //Add the contents of myInputString to the input received
                    myInputString = myBufferedReader.readLine();    //Read the next line
                }

                myInputStream.close();
                inputFromFile = myStringBuilder.toString();
            }
        }
        catch (FileNotFoundException fileNotFoundBug) //Won't compile without this method - can put in some debug messages
        {
        }
        catch (IOException IOBug) //Won't compile without this method - can put in some debugging messages
        {
        }

        return inputFromFile;
    }

    //Saves the specified "data" string to the file specified when the editEvent activity started
    private void writeToFile_Overwrite(String data) {

        try {
            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(openFileOutput(eventFileName, Context.MODE_PRIVATE));//Context.MODE_APPEND));
            //Note - changing the context to Context.MODE_APPEND adds to the end of the file, rather than overwriting it
            outputStreamWriter.write(data);
            outputStreamWriter.close();
        }
        catch (IOException e) //Won't compile without this method - can put in some debugging messages
        {
        }
    }

    //Implements the "Delete" button - deletes the event being edited and returns to the parent activity
    public void deleteEventButton(View view) {

        eventFileName = eventFileName + ".txt";

        //Generate the array of events already on file

        if(viewingEvents || monthView)  //If user was viewing events, or were viewing all events for a month
            populateTimetable_noLineBreaks(date);
        else
            populateTimetable_noLineBreaks(dayOfWeek);


        //Array will now have been generated
        //Remove the event to be replaced
        for(int i = 0; i < eventArray.size(); i++)  //Loop through the array
        {
            if(oldEventText.equals(eventArray.get(i))) {
                //Found the event to be replaced
                eventArray.remove(i);
                orderArray.remove(i);
            }
        }

        //Now, loop through the array, and take all the text from it
        String allArrayInput = "";

        for(int i = 0; i < eventArray.size(); i++)
        {
            allArrayInput = allArrayInput + eventArray.get(i);
        }

        writeToFile_Overwrite(allArrayInput);    //Save the entered values to file
        if(viewingEvents)
            shortToast(getString(R.string.noteDeleted));
        else
            shortToast(getString(R.string.classDeleted));


        //If this was the only event in the timetable, delete the file itself
        if(eventArray.size() == 0) {
            deleteEvent(eventFileName);
        }

        //Return to the ViewTimetable (or ViewMonth) activity
        Intent intent;
        if(monthView)   //Return to ViewMonth
            intent = new Intent(this, viewMonth.class);
        else    //Return to ViewTimetable
            intent = new Intent(this, ViewTimetable.class);
        String extraString = date + "END" + dayOfWeek;
        if(viewingEvents)
            extraString = extraString + "END" + "viewingEvents";
        else
            extraString = extraString + "END" + "notViewingEvents";
        intent.putExtra(MESSAGE_KEY, extraString); // Tell the Timetable activity the date to open to
        startActivity(intent);
        finish();   //Close the AddNewEvent activity, and return to the ViewTimetable activity
    }

    //Deletes the specified file
    public void deleteEvent(String myEventFileName)
    {
        //Select the file to be deleted
        File directory = getFilesDir();
        File file = new File(directory, myEventFileName);

        if(!file.exists()) //If the file does not exist
        {
            //Do nothing
        }
        else    //If the file exists
        {
            file.delete();  //Delete the file

            if (!file.exists())   //Check that the file has been successfully deleted
            {
                //Update the timetable to account for the deletion
                if(viewingEvents)
                    populateTimetable_noLineBreaks(date);
                else
                    populateTimetable_noLineBreaks(dayOfWeek);
            }
            else    //File was not deleted - an error occured
            {
                shortToast(getString(R.string.error_fileNotDeleted));
            }
        }
    }

    //Remove any END_OF_EVENT and END_OF_STRING strings from the specified "input" string
    //- used for text inputs, in case the user somehow manages to enter the exact string
    //- this is EXTREMELY unlikely!
    //This version of cleanInput also removes the END_OF_IDENTIFIER string
    public String cleanInput_specialIdentifier(String input)
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

        //Remove the END_OF_IDENTIFIER string from the input, if the user has somehow managed to enter it
        inputSplit = input.split(END_OF_IDENTIFIER);
        input = "";
        for(int i = 0; i < inputSplit.length; i++)
        {
            input = input + inputSplit[i];
        }

        return input;

    }

}
