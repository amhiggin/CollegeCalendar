package ie.tcd.scss.amhiggin.collegecalendar;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

public class ViewNotes extends ActionBarActivity {

    public final static String MESSAGE_KEY = "ie.tcd.scss.amhiggin.collegecalendar";
    //String used to parse data
    public final static String END_OF_EVENT = "E_&¦‡¶ÆæÐ♫↕ƒΔå";

    String noteFile = "myNotes";
    ArrayList<String> eventArray2 = new ArrayList<String>();
    ArrayList<String> eventArray = new ArrayList<String>();
    ArrayList<String> selectedEvents = new ArrayList<String>();
    String[] timeArray = new String[24];
    Boolean notesOnFile = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_view_notes);

        populateNotes(noteFile);

        if(notesOnFile) {
            //N.B. This ArrayAdapter code needs to be placed AFTER setContentView(R..layout.activity_view_Notes);
            ArrayAdapter<String> myAdapter = new ArrayAdapter<String>(this, R.layout.list_item, R.id.listText, eventArray);
            ListView myList = (ListView) findViewById(R.id.listView);
            myList.setOnItemClickListener(new ListClickHandler());

            myList.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
                @Override
                public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                    changeClicker();
                    return false;
                }
            });
            myList.setAdapter(myAdapter);
        }
    }

    //Puts the list into checkbox-clicking mode
    public void changeClicker() {
        ListView myList = (ListView) findViewById(R.id.listView);
        myList.setOnItemClickListener(new checkBoxClickHandler());

        CheckBox checkBox = (CheckBox) findViewById(R.id.checkBox);
        checkBox.setVisibility(View.VISIBLE);

        ArrayAdapter<String> myAdapter=new ArrayAdapter<String>(this, R.layout.list_checkbox_item, R.id.listText, eventArray);
        myList.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
        myList.setAdapter(myAdapter);

        //Make the "Cancel Deletion" button appear
        Button cancelDeletionButton = (Button) findViewById(R.id.cancelDeletionButton);
        cancelDeletionButton.setVisibility(View.VISIBLE);

        //Make the "Delete" button appear
        Button deleteButton = (Button) findViewById(R.id.deleteButton);
        deleteButton.setVisibility(View.VISIBLE);

        //Make the "Add New Event" button disappear
        Button addNewEventButton = (Button) findViewById(R.id.addNewEventButton);
        addNewEventButton.setVisibility(View.GONE);

    }

    //Puts the list back into normal clicking mode
    public void revertClicker() {
        ListView myList = (ListView) findViewById(R.id.listView);
        myList.setOnItemClickListener(new ListClickHandler());

        myList.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                changeClicker();
                return false;
            }
        });

        //Make the "Add New Event" button appear
        Button addNewEventButton = (Button) findViewById(R.id.addNewEventButton);
        addNewEventButton.setVisibility(View.VISIBLE);

        //Make the "Cancel Deletion" button disappear
        Button cancelDeletionButton = (Button) findViewById(R.id.cancelDeletionButton);
        cancelDeletionButton.setVisibility(View.GONE);

        //Make the "Delete" button disappear
        Button deleteButton = (Button) findViewById(R.id.deleteButton);
        deleteButton.setVisibility(View.GONE);

        ArrayAdapter<String> myAdapter=new ArrayAdapter<String>(this, R.layout.list_item, R.id.listText, eventArray);
        myList.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
        myList.setAdapter(myAdapter);
    }

    //Implements editing of events
    public class ListClickHandler implements AdapterView.OnItemClickListener {

        @Override
        public void onItemClick(AdapterView parent, View view,
                                int position, long id) {

            TextView listItem = (TextView) view.findViewById(R.id.listText);
            String text = listItem.getText().toString();

            String click = "";
            click = click + text;

            if(!click.equals(""))
                editNote(click);

        }
    }

    //Implements selection of multiple list elements for deletion
    public class checkBoxClickHandler implements AdapterView.OnItemClickListener {

        @Override
        public void onItemClick(AdapterView parent, View view, int position, long id) {

            TextView listItem = (TextView) view.findViewById(R.id.listText);
            String text = listItem.getText().toString();

            //String[] yetAnotherSplit = text.split("\n");
            String click ="";
            click = click + text + END_OF_EVENT;

            //selectedEvents.add(click);
            //addToSelectedEvents(click);

            if(view != null) {
                CheckBox checkBox = (CheckBox) view.findViewById(R.id.checkBox);
                checkBox.setChecked(!checkBox.isChecked());
                if(checkBox.isChecked()) {
                    addToSelectedEvents(click);
                }
                else {
                    removeFromSelectedEvents(click);
                }
            }

        }
    }

    //Add the string from an event to the selected events array
    public void addToSelectedEvents(String addition)
    {
        Boolean duplicate = false;
        for(int i = 0; i < selectedEvents.size(); i++)
        {
            if(addition.equals(selectedEvents.get(i)))
                duplicate = true;
        }
        if(!duplicate);
        selectedEvents.add(addition);

    }

    //Remove the string from an event from the selected events array
    public void removeFromSelectedEvents(String removal)
    {
        for(int i = 0; i < selectedEvents.size(); i++)
        {
            if(removal.equals(selectedEvents.get(i)))
                selectedEvents.remove(i);
        }
    }

    //User clicked "Cancel" when in deletion mode - reverts notes list to normal operation
    public void cancelDeletion(View view)
    {
        revertClicker();
        selectedEvents.clear();
        //Make the "Cancel Deletion" button disappear
        Button cancelDeletionButton = (Button) findViewById(R.id.cancelDeletionButton);
        cancelDeletionButton.setVisibility(View.GONE);

        Button deleteButton = (Button) findViewById(R.id.deleteButton);
        deleteButton.setVisibility(View.GONE);
    }

    //Deletes all notes that have been marked for deletion by the user
    public void deleteSelectedEvents()
    {
        //Generate the array of notes saved on file
        populateNotes_noLineBreaks(noteFile);

        //The selectedEvents ArrayList contains the strings matching the events to be deleted

        //Loop through all of the selectedEvents
        for(int selectedEvents_i = 0; selectedEvents_i < selectedEvents.size(); selectedEvents_i++)
        {
            //Loop through the eventArray for each item in selectedEvents
            for(int eventArray_i = 0; eventArray_i < eventArray2.size(); eventArray_i++)
            {
                if(selectedEvents.get(selectedEvents_i).equals(eventArray2.get(eventArray_i))) {
                    //This item in eventArray matches one of the selected events
                    eventArray2.remove(eventArray_i);
                }
            }
        }

        //Now, loop through the array, and take all the text from it
        String allArrayInput = "";

        for(int i = 0; i < eventArray2.size(); i++)
        {
            allArrayInput = allArrayInput + eventArray2.get(i);
        }

        String noteFileName;
        noteFileName = noteFile + ".txt";

        writeToFile_Overwrite(allArrayInput, noteFileName);    //Save the entered values to file
        shortToast(getString(R.string.noteDeleted));

        //If this was the only event in the Notes, delete the file itself
        if(eventArray2.size() == 0)
            deleteEvent(noteFileName);

        selectedEvents.clear();
    }

    //Populates the contents of the Notes to match saved values (if any exist) for the selected day
    public void populateNotes(final String fileName)
    {
        String noteFileName;

        noteFileName = fileName + ".txt";

        String myString = readFromFile(noteFileName);  //Search the internal storage for data corresponding to noteFileName.txt
        if(myString.equals("")) //No events for this file
        {
            notesOnFile = false;
            TextView noEvents_textView = (TextView) findViewById(R.id.noEventsSaved_textView);
            noEvents_textView.setText(getText(R.string.noNotesSaved));
        }
        String arrayInput = "";

        String[] myOtherSplit = myString.split(END_OF_EVENT);

        for(int i = 0; i < myOtherSplit.length; i++)
        {
            eventArray.add(i, myOtherSplit[i]);
        }

        if(eventArray.size() != 1) {
            //Set the Notes to display its contents
            //ArrayAdapter<String> myAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, eventArray);
            ArrayAdapter<String> myAdapter = new ArrayAdapter<String>(this, R.layout.list_item, R.id.listText, eventArray);
            ListView myList = (ListView) findViewById(R.id.listView);
            myList.setOnItemClickListener(new ListClickHandler());
            myList.setAdapter(myAdapter);
        }
    }

    //Reads values from the saved file. Used in populateNotes()
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

    //Implements the "Delete all" button, which deletes all events for the day, clearing the Notes
    public void deleteButton(View view)
    {
        revertClicker();
        deleteSelectedEvents();

        //Restart the activity
        Intent intent = getIntent();
        startActivity(intent);
        finish();
    }

    //Implements the addNewEventButton, which adds a new event to the Notes
    public void addNewEventButton(View view)
    {
        Intent intent = new Intent(this, AddNewNote.class);
        startActivity(intent);
        finish();
    }

    //Deletes an event's file and updates the Notes to account for any changes
    public void deleteEvent(String mynoteFileName)
    {
        //Select the file to be deleted
        File directory = getFilesDir();
        File file = new File(directory, mynoteFileName);

        if(!file.exists()) //If the file does not exist
        {
            //Do nothing
        }
        else    //If the file exists
        {
            file.delete();  //Delete the file

            if (!file.exists())   //Check that the file has been successfully deleted
                populateNotes(noteFile);
            else    //File was not deleted - an error occured
                shortToast(getString(R.string.error_fileNotDeleted));
        }
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

    //Takes the user to the editNote activity
    public void editNote(String message)
    {
        Intent intent = new Intent(this, editNote.class);
        //Sample message: notes
        intent.putExtra(MESSAGE_KEY, message);
        startActivity(intent);
        finish();
    }

    //Populates the contents of the list to match saved values (if any exist) - used for deletion of selected notes
    public void populateNotes_noLineBreaks(final String fileName)
    {
        String noteFileName;

        noteFileName = fileName + ".txt";

        String myString = readFromFile(noteFileName);  //Search the internal storage for data corresponding to noteFileName.txt
        String arrayInput = "";

        String[] myOtherSplit = myString.split(END_OF_EVENT); //Split into separate notes

        for(int i = 0; i < myOtherSplit.length; i++)
        {
            arrayInput = myOtherSplit[i] + END_OF_EVENT;   //e.g. arrayInput = date/timeEND_OF_STRINGnotesEND_OF_EVENT
            eventArray2.add(arrayInput);
        }

    }

    //Saves the specified "data" string to the specified file
    private void writeToFile_Overwrite(String data, String noteFileName) {

        try {
            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(openFileOutput(noteFileName, Context.MODE_PRIVATE));//Context.MODE_APPEND));
            //Note - changing the context to Context.MODE_APPEND adds to the end of the file, rather than overwriting it
            outputStreamWriter.write(data);
            outputStreamWriter.close();
        }
        catch (IOException e) //Won't compile without this method - can put in some debugging messages
        {
        }
    }

}