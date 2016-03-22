package ie.tcd.scss.amhiggin.collegecalendar;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.sql.Time;
import java.util.Calendar;

public class AddNewNote extends AppCompatActivity {

    public final static String MESSAGE_KEY = "ie.tcd.scss.amhiggin.collegecalendar";
    //Strings used to parse data
    public final static String END_OF_STRING = "S_&¦‡¶ÆæÐ♫↕ƒΔå";
    public final static String END_OF_EVENT = "E_&¦‡¶ÆæÐ♫↕ƒΔå";

    String eventFileName = "myNotes";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_add_new_note);

    }

    //Implements the saveEventButton - saves the event data and ends the AddNewNote activity
    public void saveEventButton(View view) {

        String note_text, note_complete;

        EditText timeBox = (EditText) findViewById(R.id.timeBox);

        //Get the input entered by the user
        note_text = timeBox.getText().toString();
        note_text = cleanInput(note_text);

        if(note_text.equals(""))
            note_text = getString(R.string.noNoteEntered);

        note_complete = note_text + END_OF_EVENT;

        //Use the entered time to determine which event file to save the event to
        eventFileName = eventFileName + ".txt";

        writeToFile(note_complete, eventFileName);    //Save the entered values to the event file
        shortToast(getString(R.string.noteSaved));

        //Return to the ViewNotes activity
        Intent intent = new Intent(this, ViewNotes.class);
        startActivity(intent);
        finish();   //Close the AddNewNote activity

    }

    //Saves the specified "data" string to the file specified when the AddNewNote activity started
    private void writeToFile(String data, String fileName) {

        try {
            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(openFileOutput(fileName, Context.MODE_APPEND));
            //Note - changing the context to Context.MODE_APPEND adds to the end of the file, rather than overwriting it
            outputStreamWriter.write(data);
            outputStreamWriter.close();
        }
        catch (IOException e) //Won't compile without this method - can put in some debugging messages
        {
        }
    }

    //Implements the cancel button - ends the AddNewNote activity
    public void cancelButton(View view) {

        //Return to the ViewTimetable activity
        Intent intent = new Intent(this, ViewNotes.class);
        startActivity(intent);
        finish();   //Close the AddNewNote activity, and return to the ViewNotes activity
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