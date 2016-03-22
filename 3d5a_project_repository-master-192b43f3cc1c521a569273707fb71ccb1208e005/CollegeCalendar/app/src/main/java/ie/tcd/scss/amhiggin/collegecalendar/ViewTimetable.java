package ie.tcd.scss.amhiggin.collegecalendar;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
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
import java.lang.reflect.Array;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

public class ViewTimetable extends ActionBarActivity {

    public final static String MESSAGE_KEY = "ie.tcd.scss.amhiggin.collegecalendar";
    //Strings used to parse save data
    public final static String END_OF_STRING = "S_&¦‡¶ÆæÐ♫↕ƒΔå";
    public final static String END_OF_EVENT = "E_&¦‡¶ÆæÐ♫↕ƒΔå";
    public final static String END_OF_IDENTIFIER = "I_&¦‡¶ÆæÐ♫↕ƒΔå";

    //Day of the week, date, array for event list, array for times, boolean for view the user is in (events or classes)
    String dayOfWeek;
    String date;
    ArrayList<String> eventArray2 = new ArrayList<String>();
    ArrayList<Integer> orderArray2 = new ArrayList<>();
    ArrayList<String> eventArray = new ArrayList<String>();
    ArrayList<String> selectedEvents = new ArrayList<String>();
    ArrayList<Integer> orderArray = new ArrayList<>();
    Boolean viewingEvents;
    Boolean eventsOnFile = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Get the intent, which contains <date>END<dayOfWeek>END<viewingEvents/notViewingEvents>, e.g. 4-12-2015ENDFridayENDviewingEvents
        Intent dateIntent = getIntent();
        date = dateIntent.getStringExtra(DisplayCalendar.MESSAGE_KEY);

        //Split the intent message into date, dayOfWeek and viewingEvents/notViewingEvents
        String[] mySplit = date.split("END");

        date = mySplit[0];
        dayOfWeek = mySplit[1];

        if(mySplit[2].equals("viewingEvents"))
            viewingEvents = true;
        else
            viewingEvents = false;

        setContentView(R.layout.activity_view_timetable);
        TextView viewTitle = (TextView) findViewById(R.id.viewTitle);

        if(viewingEvents) {
            //User is looking at events
            populateTimetable(date);
            viewTitle.setText(getString(R.string.events));

            /*Button monthButton = (Button) findViewById(R.id.viewMonthButton);
            monthButton.setVisibility(View.VISIBLE);*/

            Button viewButton = (Button) findViewById(R.id.viewClassesButton);
            viewButton.setVisibility(View.VISIBLE);

            viewButton = (Button) findViewById(R.id.viewEventsButton);
            viewButton.setVisibility(View.GONE);
        }
        else {
            //User is looking at classes
            populateTimetable(dayOfWeek);
            viewTitle.setText(getString(R.string.classes));
            TextView addNewEventButton = (TextView) findViewById(R.id.addNewEventButton);
            addNewEventButton.setText(getString(R.string.addNewClass_button));

            Button monthButton = (Button) findViewById(R.id.viewMonthButton);
            monthButton.setVisibility(View.GONE);

            Button viewButton = (Button) findViewById(R.id.viewClassesButton);
            viewButton.setVisibility(View.GONE);

            viewButton = (Button) findViewById(R.id.viewEventsButton);
            viewButton.setVisibility(View.VISIBLE);
        }

        //Set the day of the week to be displayed
        setDayOfWeekBox(dayOfWeek);

        //If there is any saved data for this timetable
        if(eventsOnFile) {
            //N.B. This ArrayAdapter code needs to be placed AFTER setContentView(R..layout.activity_view_timetable);
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

    //Puts the list into checkbox-clicking mode - for deletion of multiple events
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

        //Make the "View events" button disappear
        Button viewEventsButton = (Button) findViewById(R.id.viewEventsButton);
        viewEventsButton.setVisibility(View.GONE);

        //Make the "View classes" button disappear
        Button viewClassesButton = (Button) findViewById(R.id.viewClassesButton);
        viewClassesButton.setVisibility(View.GONE);

        //Make the "View Month" button disappear
        Button viewMonthButton = (Button) findViewById(R.id.viewMonthButton);
        viewMonthButton.setVisibility(View.GONE);

    }

    //Puts the list back into normal clicking mode - exit deletion mode
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

        //Make the "View Month" button appear
        Button viewMonthButton = (Button) findViewById(R.id.viewMonthButton);
        viewMonthButton.setVisibility(View.VISIBLE);

        //Make the "Cancel Deletion" button disappear
        Button cancelDeletionButton = (Button) findViewById(R.id.cancelDeletionButton);
        cancelDeletionButton.setVisibility(View.GONE);

        //Make the "Delete" button disappear
        Button deleteButton = (Button) findViewById(R.id.deleteButton);
        deleteButton.setVisibility(View.GONE);

        if(viewingEvents) {
            //Make the "View classes" button appear
            Button viewClassesButton = (Button) findViewById(R.id.viewClassesButton);
            viewClassesButton.setVisibility(View.VISIBLE);
        }
        else{
            //Make the "View events" button appear
            Button viewEventsButton = (Button) findViewById(R.id.viewEventsButton);
            viewEventsButton.setVisibility(View.VISIBLE);
        }

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

            String[] yetAnotherSplit = text.split("\n");
            String click ="";
            if(yetAnotherSplit.length == 1) //Split only contains one element - only a time was entered
                click = click + yetAnotherSplit[0];
            if(yetAnotherSplit.length == 2) //Split only contains one element - only a time was entered
                click = click + yetAnotherSplit[0] +END_OF_STRING + yetAnotherSplit[1] + END_OF_STRING;
            if(yetAnotherSplit.length == 3)
                click = click + yetAnotherSplit[0] + END_OF_STRING + yetAnotherSplit[1] + END_OF_STRING + yetAnotherSplit[2];

            if(!click.equals(""))
                editEvent(click);

        }
    }

    //Implements selection of multiple list elements for deletion
    public class checkBoxClickHandler implements AdapterView.OnItemClickListener {

        @Override
        public void onItemClick(AdapterView parent, View view, int position, long id) {

            TextView listItem = (TextView) view.findViewById(R.id.listText);
            String text = listItem.getText().toString();

            String[] yetAnotherSplit = text.split("\n");
            String click ="";
            if(yetAnotherSplit.length == 1) //Split only contains one element - only a time was entered
                click = click + yetAnotherSplit[0] + END_OF_STRING + END_OF_STRING + END_OF_STRING;
            if(yetAnotherSplit.length == 2) //Split only contains one element - only a time was entered
                click = click + yetAnotherSplit[0] +END_OF_STRING + yetAnotherSplit[1] + END_OF_STRING + END_OF_STRING;
            if(yetAnotherSplit.length == 3)
                click = click + yetAnotherSplit[0] + END_OF_STRING + yetAnotherSplit[1] + END_OF_STRING + yetAnotherSplit[2] + END_OF_STRING;
            click = click + END_OF_EVENT;

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

    //Mark an event for deletion
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

    //Undo the marking of an event for deltion
    public void removeFromSelectedEvents(String removal)
    {
        for(int i = 0; i < selectedEvents.size(); i++)
        {
            if(removal.equals(selectedEvents.get(i)))
                selectedEvents.remove(i);
        }
    }

    //User clicked the "Cancel" button when in deletion mode - go back to normal opertaion
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

    //User clicked the "Delete" button - delete any events that have been marked for deletion
    public void deleteSelectedEvents()
    {
        //First, generate an array of all events on file - with no newline characters
        if(viewingEvents)
            populateTimetable_noLineBreaks(date);
        else
            populateTimetable_noLineBreaks(dayOfWeek);

        //The selectedEvents ArrayList contains the strings matching the events to be deleted

        //Loop through all of the selectedEvents
        for(int selectedEvents_i = 0; selectedEvents_i < selectedEvents.size(); selectedEvents_i++)
        {
            //For each item in selectedEvents, loop through the eventArray
            for(int eventArray_i = 0; eventArray_i < eventArray2.size(); eventArray_i++)
            {
                if(selectedEvents.get(selectedEvents_i).equals(eventArray2.get(eventArray_i))) {
                    //This item in eventArray matches one of the selected events
                    // - remove it from the new eventArray2 and orderArray2
                    eventArray2.remove(eventArray_i);
                    orderArray2.remove(eventArray_i);
                }
            }
        }

        //Now, loop through the new array of event - eventArray2, and take all the text from it
        String allArrayInput = "";

        for(int i = 0; i < eventArray2.size(); i++)
        {
            allArrayInput = allArrayInput + eventArray2.get(i);
        }

        String eventFileName;
        if(viewingEvents)
            eventFileName = date + ".txt";
        else
            eventFileName = dayOfWeek + ".txt";

        writeToFile_Overwrite(allArrayInput, eventFileName);    //Save the entered values to file - overwrite what was previously saved
        shortToast(getString(R.string.eventDeleted));

        //If all of the events in the timetable have been deleted, delete the save file itself
        if(eventArray2.size() == 0)
            deleteEvent(eventFileName);

        selectedEvents.clear();
        orderArray2.clear();

    }

    //Populates the contents of the timetable to match saved values (if any exist) for the selected day
    public void populateTimetable(final String selected_date)
    {
        //Set the date to be displayed at the top of the timetable
        TextView dateTitle = (TextView) findViewById(R.id.dateTitle);
        dateTitle.setText(date);

        String eventFileName;

        eventFileName = selected_date + ".txt";

        String myString = readFromFile(eventFileName);  //Search the internal storage for data corresponding to eventFileName.txt
        if(myString.equals("")) //No events for this file
        {
            eventsOnFile = false;
            TextView noEvents_textView = (TextView) findViewById(R.id.noEventsSaved_textView);
            if(viewingEvents)
                noEvents_textView.setText(getText(R.string.noEventsSaved));
            else
                noEvents_textView.setText(getText(R.string.noClassesSaved));
        }
        String arrayInput = "";

        //Split the save data into individual events
        String[] myOtherSplit = myString.split(END_OF_EVENT);

        for(int i = 0; i < myOtherSplit.length; i++)
        {
            //Split the event data into time, title and location
            String[] mySplit = myOtherSplit[i].split(END_OF_STRING);

            if(mySplit.length == 1) //Split only contains one element - only a time was entered
                arrayInput = arrayInput + mySplit[0];

            if(mySplit.length == 2) //Split contains two elements (either title or location are blank)
                arrayInput = arrayInput + mySplit[0] + "\n" + mySplit[1];

            if(mySplit.length == 3) //Split contains all three elements - title, location and date
                arrayInput = arrayInput + mySplit[0] + "\n" + mySplit[1] + "\n" + mySplit[2];

            //Place time in orderArray - use the contents of orderArray to order the listView events
            Integer orderInput;
            if(mySplit[0].equals(""))   //Blank file - no time to take from the split
                orderInput = 0;
            else
            {
                //mySplit[0] example --> 14:59
                String[] myTimeSplit = mySplit[0].split(":");
                orderInput = Integer.valueOf(myTimeSplit[0] + myTimeSplit[1]);
                //e.g. orderInput --> 1459
            }
            Boolean added = false;
            int j = 0;

            //Add the event to the eventArray - use the orderArray to determine where it should be placed in the array
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

        //Set the timetable to display its contents
        if(eventArray.size() != 1) {
            ArrayAdapter<String> myAdapter = new ArrayAdapter<String>(this, R.layout.list_item, R.id.listText, eventArray);
            ListView myList = (ListView) findViewById(R.id.listView);
            myList.setOnItemClickListener(new ListClickHandler());
            myList.setAdapter(myAdapter);
        }
    }

    //Reads values from the saved file. Used in populateTimetable()
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

    //Implements the "Delete" button, which deletes any events the user has selected
    public void deleteButton(View view)
    {

        revertClicker();    //Go back to normal clicking mode
        deleteSelectedEvents();

        //Restart the activity
        Intent intent = getIntent();
        startActivity(intent);
        finish();
    }

    //Implements the addNewEventButton, which adds a new event to the timetable
    public void addNewEventButton(View view)
    {
        Intent intent = new Intent(this, AddNewEvent.class);
        String extraString = date + "END" + dayOfWeek;
        if(viewingEvents)
            extraString = extraString + "END" + "viewingEvents";
        else
            extraString = extraString + "END" + "viewingClasses";
        intent.putExtra(MESSAGE_KEY, extraString); // Tell the AddNewEvent activity the date to save the event to
        startActivity(intent);
        finish();
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
                if(viewingEvents)
                    populateTimetable(date);
                else
                    populateTimetable(dayOfWeek);
            }
            else    //File was not deleted - an error occurred
            {
                shortToast(getString(R.string.error_fileNotDeleted));
            }
        }
    }

    //Display a short Toast message
    public void shortToast(String message)
    {
        Context context = getApplicationContext();
        CharSequence text = message;
        int duration = Toast.LENGTH_LONG;
        Toast toast = Toast.makeText(context, text, duration);
        toast.show();
    }

    //Set the text field that displays the day of the week of the timetable being viewed
    public void setDayOfWeekBox(String myDay)
    {
        TextView dayOfWeekSlot = (TextView) findViewById(R.id.dayOfWeekBox);
        dayOfWeekSlot.setText(myDay);
    }

    //Button that takes the user to tomorrow's timetable
    public void viewTomorrowTimetable(View view) {

        try {
            //Split the date into three individual numbers (remove the hyphens between them)
            String[] mySplit = date.split("-");

            String dayOfMonth = mySplit[0];
            String month = mySplit[1];
            String year = mySplit[2];

            String newDateString = year + "-" + month + "-" + dayOfMonth;
            SimpleDateFormat newDate = new SimpleDateFormat("yyyy-MM-dd");
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(newDate.parse(newDateString));
            calendar.add(Calendar.DATE, 1);

            int dayOfWeek = calendar.get(calendar.DAY_OF_WEEK);
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

            //Find the new date
            int dayOfMonth_int = calendar.get(calendar.DAY_OF_MONTH);
            int month_int = calendar.get(calendar.MONTH);
            int year_int = calendar.get(calendar.YEAR);

            //Set the new date as a string
            month_int++;    //The months in the Android calendar range from 0-11, rather than 1-12
            String complete_date = Integer.toString(dayOfMonth_int) + "-" + Integer.toString(month_int) + "-" + Integer.toString(year_int);

            //Concatenate the date and day of week strings, and pass them to the timetable activity
            String finalExtra = complete_date + "END" + dayOfWeekString;    //e.g. 24-11-2015ENDMonday
            if(viewingEvents)
                finalExtra = finalExtra + "END" + "viewingEvents";
            else
                finalExtra = finalExtra + "END" + "notViewingEvents";
            Intent intent = new Intent(this, ViewTimetable.class);
            intent.putExtra(MESSAGE_KEY, finalExtra);
            startActivity(intent);
            finish();
        }

        catch (java.text.ParseException fileNotFoundBug) //Won't compile without this method - can put in some debug messages
        {
        }

    }

    //Button that takes the user to yesterday's timetable
    public void viewYesterdayTimetable(View view) {

        try {
            //Split the date into individual numbers (remove the hyphens between them)
            String[] mySplit = date.split("-");

            String dayOfMonth = mySplit[0];
            String month = mySplit[1];
            String year = mySplit[2];

            String newDateString = year + "-" + month + "-" + dayOfMonth;
            SimpleDateFormat newDate = new SimpleDateFormat("yyyy-MM-dd");
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(newDate.parse(newDateString));
            calendar.add(Calendar.DATE, -1);
            newDateString = newDate.format(calendar.getTime());

            int dayOfWeek = calendar.get(calendar.DAY_OF_WEEK);
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

            //Find the new date
            int dayOfMonth_int = calendar.get(calendar.DAY_OF_MONTH);
            int month_int = calendar.get(calendar.MONTH);
            int year_int = calendar.get(calendar.YEAR);

            //Set the new date as a string
            month_int++;    //The months in the Android calendar range from 0-11, rather than 1-12
            String complete_date = Integer.toString(dayOfMonth_int) + "-" + Integer.toString(month_int) + "-" + Integer.toString(year_int);

            //Concatenate the date and day of week strings, and pass them to the timetable activity
            String finalExtra = complete_date + "END" + dayOfWeekString;    //e.g. 24-11-2015ENDMonday
            if(viewingEvents)
                finalExtra = finalExtra + "END" + "viewingEvents";
            else
                finalExtra = finalExtra + "END" + "notViewingEvents";
            Intent intent = new Intent(this, ViewTimetable.class);
            intent.putExtra(MESSAGE_KEY, finalExtra);
            startActivity(intent);
            finish();
        }

        catch (java.text.ParseException fileNotFoundBug) //Won't compile without this method - can put in some debug messages
        {
        }
    }

    //Restart the timetable - set it to display classes
    public void viewClasses(View view)
    {
        Intent intent = new Intent(this, ViewTimetable.class);
        String extraString = date + "END" + dayOfWeek + "END" + "notViewingEvents";   //Restart the timetable, with it set to display classes
        intent.putExtra(MESSAGE_KEY, extraString);
        startActivity(intent);
        finish();
    }

    //Restart the timetable - set it to display events
    public void viewEvents(View view)
    {
        Intent intent = new Intent(this, ViewTimetable.class);
        String extraString = date + "END" + dayOfWeek + "END" + "viewingEvents";   //Restart the timetable, with it set to display events
        intent.putExtra(MESSAGE_KEY, extraString);
        startActivity(intent);
        finish();
    }

    //Takes the user to the editEvent activity
    public void editEvent(String message)
    {
        Intent intent = new Intent(this, editEvent.class);
        //Sample message: 10-12-2015ENDThursdayENDviewingEventsEND_OF_IDENTIFIER9END_OF_STRINGTitleEND_OF_STRINGLocation
        if(viewingEvents)
            message = date + "END" + dayOfWeek + "END" + "viewingEvents" + END_OF_IDENTIFIER + message;
        else
            message = date + "END" + dayOfWeek + "END" + "notViewingEvents" + END_OF_IDENTIFIER + message;
        intent.putExtra(MESSAGE_KEY, message);
        startActivity(intent);
        finish();
    }

    //Takes the user back to the month schedule
    public void viewMonthButton(View view)
    {
        Intent intent = new Intent(this, viewMonth.class);
        String extraString = date + "END" + dayOfWeek + "END" + "viewingEvents";   //The month view only displays events
        intent.putExtra(MESSAGE_KEY, extraString);
        startActivity(intent);
        finish();
    }

    //Populates the contents of the timetable to match saved values (if any exist) for the selected day - used for deletion of selected events
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

            arrayInput = myOtherSplit[i] + END_OF_EVENT;   //e.g. arrayInput = timeEND_OF_STRINGtitleEND_OF_STRINGlocationEND_OF_STRINGEND_OF_EVENT

            //Place time in orderArray - use the contents of orderArray to order the listView events
            Integer orderInput;
            if(mySplit[0].equals(""))   //Blank file - no time to take from the split
                orderInput = 0;
            else
            {
                //mySplit[0] example: 14:59
                String[] myTimeSplit = mySplit[0].split(":");
                orderInput = Integer.valueOf(myTimeSplit[0] + myTimeSplit[1]);
                //orderInput = Integer.valueOf(mySplit[0]);
            }
            Boolean added = false;
            int j = 0;
            while(!added && j < orderArray2.size() )
            {
                if(orderInput < orderArray2.get(j)) {
                    orderArray2.add(j, orderInput);  //Add the time to the orderArray, at a certain position
                    added = true;

                    eventArray2.add(j, arrayInput);  //Add the event at the same position in the eventArray
                    arrayInput = "";
                }
                j++;
            }

            if(!added)  //The list was blank - this was the first entry
            {
                orderArray2.add(orderInput);

                eventArray2.add(arrayInput);
                arrayInput = "";
            }
        }

    }

    //Saves the specified "data" string to the file specified when the editEvent activity started
    private void writeToFile_Overwrite(String data, String eventFileName) {

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

}
