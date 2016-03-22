package ie.tcd.scss.amhiggin.collegecalendar;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;

public class EditToDoItem extends AppCompatActivity {

    public final static String MESSAGE_KEY = "ie.tcd.scss.amhiggin.collegecalendar";
    public final static String END_OF_STRING = "S_&¦‡¶ÆæÐ♫↕ƒΔå";
    public final static String END_OF_EVENT = "END_OF_ITEM";

    String date;
    String dayOfWeek;
    String eventFileName;
    String[] validTimeArray = new String[24];    //Valid times for input
    Boolean viewingEvents;
    ArrayList<String> eventArray = new ArrayList<String>();
    ArrayList<Integer> orderArray = new ArrayList<>();
    String oldEventText;
    Boolean monthView = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_to_do_item);

        Intent ItemIntent = getIntent();
        String message = ItemIntent.getStringExtra(ToDoList.MESSAGE_KEY);

        date = "myToDoList";

        EditText timeBox = (EditText) findViewById(R.id.timeBox);
        if(message.equals(getString(R.string.noItemsSaved)))
        {
            //Do nothing
        }
        else
            timeBox.setText(message);


        //Make a record of the event being edited
        oldEventText = message + END_OF_EVENT;

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

    //Implements the cancel button - ends the editNote activity
    public void cancelButton(View view) {

        Intent intent = new Intent(this, ToDoList.class);
        startActivity(intent);
        finish();   //Close the AddNewEvent activity, and return to the ViewTimetable activity
    }

    //Implements the saveEventButton - saves the event data and ends the AddNewEvent activity
    public void saveEventButton(View view) {

        String message;
        EditText timeBox = (EditText) findViewById(R.id.timeBox);

        //Get the input entered by the user
        message = timeBox.getText().toString();
        message = cleanInput(message);

        if(message.equals(""))
            message = getString(R.string.noItemsSaved);
        message = message + END_OF_EVENT;

        populateTimetable_noLineBreaks(date);

        //Array will now have been generated
        //Remove the old version of the note
        for(int i = 0; i < eventArray.size(); i++)  //Loop through the array
        {
            if(oldEventText.equals(eventArray.get(i))) {
                eventArray.remove(i);
            }
        }

        //Now add the new version of the note
        eventArray.add(message);

        //Now, loop through the array, and take all the text from it
        String allArrayInput = "";

        for(int i = 0; i < eventArray.size(); i++)
        {
            allArrayInput = allArrayInput + eventArray.get(i);
        }

        writeToFile_Overwrite(allArrayInput);    //Save the entered values
        shortToast(getString(R.string.changesSaved));

        Intent intent = new Intent(this, ToDoList.class);
        startActivity(intent);
        finish();   //Close the AddNewNote activity, and return to the ViewNotes activity
    }

    //Populates the contents of the timetable to match saved values (if any exist) for the selected day
    public void populateTimetable_noLineBreaks(final String selected_date)
    {

        eventFileName = selected_date + ".txt";

        String myString = readFromFile(eventFileName);  //Search the internal storage for data corresponding to eventFileName.txt
        String arrayInput = "";

        String[] myOtherSplit = myString.split(END_OF_EVENT);
        //e.g. split into multiple timeEND_OF_STRINGtitleEND_OF_STRINGlocation strings

        for(int i = 0; i < myOtherSplit.length; i++)
        {
            arrayInput = myOtherSplit[i] + END_OF_EVENT;   //e.g. arrayInput = timeEND_OF_STRINGtitleEND_OF_STRINGlocationEND_OF_EVENT
            eventArray.add(arrayInput);
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
            eventFileName = date + ".txt";
            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(openFileOutput(eventFileName, Context.MODE_PRIVATE));//Context.MODE_APPEND));
            //Note - changing the context to Context.MODE_APPEND adds to the end of the file, rather than overwriting it
            outputStreamWriter.write(data);
            outputStreamWriter.close();
        }
        catch (IOException e) //Won't compile without this method - can put in some debugging messages
        {
        }
    }

    public void deleteEventButton(View view) {

        //Use the entered time to determine which event file to delete from
        eventFileName = date + ".txt";

        //Generate the array of events already on file
        populateTimetable_noLineBreaks(date);

        //Array will now have been generated
        //Remove the event to be deleted
        for(int i = 0; i < eventArray.size(); i++)  //Loop through the array
        {
            if(oldEventText.equals(eventArray.get(i))) {
                eventArray.remove(i);
            }
        }

        //Now, loop through the array, and take all the text from it
        String allArrayInput = "";

        for(int i = 0; i < eventArray.size(); i++)
        {
            allArrayInput = allArrayInput + eventArray.get(i);
        }

        writeToFile_Overwrite(allArrayInput);    //Save the entered values to file
        shortToast(getString(R.string.noteDeleted));

        //If this was the only event in the timetable, delete the file itself
        if(eventArray.size() == 0) {
            deleteEvent(eventFileName);
        }

        Intent intent = new Intent(this, ToDoList.class);
        startActivity(intent);
        finish();   //Close the AddNewEvent activity, and return to the ViewTimetable activity
    }

    //Deletes an event's file and updates the timetable to account for any changes
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
                populateTimetable_noLineBreaks(date);
            }
            else    //File was not deleted - an error occured
            {
                shortToast(getString(R.string.error_fileNotDeleted));
            }
        }
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
