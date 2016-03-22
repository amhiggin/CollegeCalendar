package ie.tcd.scss.amhiggin.collegecalendar;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

public class viewMonth extends AppCompatActivity {

    public final static String MESSAGE_KEY = "ie.tcd.scss.amhiggin.collegecalendar";
    //Strings used to parse data
    public final static String END_OF_STRING = "S_&¦‡¶ÆæÐ♫↕ƒΔå";
    public final static String END_OF_EVENT = "E_&¦‡¶ÆæÐ♫↕ƒΔå";

    String date;
    String month;
    String dayOfWeek;
    ArrayList<String> eventArray = new ArrayList<String>();
    ArrayList<Integer> orderArray = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_month);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //Get the name of the file the event is to be saved to
        Intent dateIntent = getIntent();    //Sample message: 10-12-15ENDThursdayENDviewingEvents
        date = dateIntent.getStringExtra(ViewTimetable.MESSAGE_KEY);

        //Split the received intent into individual parts
        String[] mySplit = date.split("END");
        date = mySplit[0];
        dayOfWeek = mySplit[1];

        //Find the month to open to
        String[] monthSplit = date.split("-");
        month = monthSplit[1] + "-" + monthSplit[2];   //e.g. 12-2015

        setContentView(R.layout.activity_view_month);

        TextView viewTitle = (TextView) findViewById(R.id.viewTitle);

        //Set the month to be displayed at the top, e.g. December 2015
        switch(monthSplit[1])
        {
            case("1"): {
                viewTitle.setText(Html.fromHtml("<b>" + getString(R.string.January) + " " + monthSplit[2] + "</b>"));
                break;
            }
            case("2"): {
                viewTitle.setText(Html.fromHtml("<b>" + getString(R.string.February) + " " + monthSplit[2] + "</b>"));
                break;
            }
            case("3"): {
                viewTitle.setText(Html.fromHtml("<b>" + getString(R.string.March) + " " + monthSplit[2] + "</b>"));
                break;
            }
            case("4"): {
                viewTitle.setText(Html.fromHtml("<b>" + getString(R.string.April) + " " + monthSplit[2] + "</b>"));
                break;
            }
            case("5"): {
                viewTitle.setText(Html.fromHtml("<b>" + getString(R.string.May) + " " + monthSplit[2] + "</b>"));
                break;
            }
            case("6"): {
                viewTitle.setText(Html.fromHtml("<b>" + getString(R.string.June) + " " + monthSplit[2] + "</b>"));
                break;
            }
            case("7"): {
                viewTitle.setText(Html.fromHtml("<b>" + getString(R.string.July) + " " + monthSplit[2] + "</b>"));
                break;
            }
            case("8"): {
                viewTitle.setText(Html.fromHtml("<b>" + getString(R.string.August) + " " + monthSplit[2] + "</b>"));
                break;
            }
            case("9"): {
                viewTitle.setText(Html.fromHtml("<b>" + getString(R.string.September) + " " + monthSplit[2] + "</b>"));
                break;
            }
            case("10"): {
                viewTitle.setText(Html.fromHtml("<b>" + getString(R.string.October) + " " + monthSplit[2] + "</b>"));
                break;
            }
            case("11"): {
                viewTitle.setText(Html.fromHtml("<b>" + getString(R.string.November) + " " + monthSplit[2] + "</b>"));
                break;
            }
            case("12"): {
                viewTitle.setText(Html.fromHtml("<b>" + getString(R.string.December) + " " + monthSplit[2] + "</b>"));
                break;
            }
        }

        populateTimetable(month);

        //N.B The item click listener will cause a crash unless this ArrayAdapter code
        // is not placed in THIS SPECIFIC LOCATION (in addition to anywhere else it is
        // placed within the code)
        ArrayAdapter<String> myAdapter=new ArrayAdapter<String>(this, R.layout.list_item, R.id.listText, eventArray);
        ListView myList=(ListView) findViewById(R.id.listView);
        myList.setOnItemClickListener(new ListClickHandler());
        myList.setAdapter(myAdapter);
    }

    //If a user clicks on a date in the list, take them to the timetable for that day
    public class ListClickHandler implements AdapterView.OnItemClickListener {

        @Override


        public void onItemClick(AdapterView parent, View view,
                                int position, long id) {

            TextView myListItem = (TextView) view.findViewById(R.id.listText);
            String text = myListItem.getText().toString();

            //If user clicked on a date slot (and not an event slot), take them to the appropriate date
            for (int i = 1; i < 32; i++) {
                if (text.equals(String.valueOf(i) + "-" + month))
                    viewTimetable(text);
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

    //Populates the contents of the timetable to match saved values (if any exist) for the selected day
    public void populateTimetable(final String selected_month)
    {
        String eventFileName;
        Boolean noEvents = true;

        for(int i = 1; i < 32; i++)     //e.g. 1-12-1015.txt --> 31-12-2015.txt
        {
            eventFileName = String.valueOf(i) + "-" + selected_month + ".txt";
            String myString = "" + readFromFile(eventFileName);  //Search the internal storage for data corresponding to eventFileName.txt

            //If the file exists - there are saved events for this day
            if(!(myString.equals("")))
            {
                noEvents = false;

                //Add the date to the list
                eventArray.add(String.valueOf(i) + "-" + selected_month);
                int indexOffset = eventArray.size();

                //Add all of the events, in the right order for that date
                String[] myOtherSplit = myString.split(END_OF_EVENT); //Split up the saved data into individual events

                for(int j = 0; j < myOtherSplit.length; j++)    //Loop through each of the events saved on file
                {
                    String arrayInput = "";
                    String[] mySplit = myOtherSplit[j].split(END_OF_STRING);  //Split up events into into time, title and location

                    if(mySplit.length == 1) //Split only contains one element - only a time was entered
                        arrayInput = arrayInput + mySplit[0];

                    if(mySplit.length == 2) //Split contains two elements (either title or location are blank)
                        arrayInput = arrayInput + mySplit[0] + "\n" + mySplit[1];

                    if(mySplit.length == 3) //Split contains all three elements - title, location and date
                        arrayInput = arrayInput + mySplit[0] + "\n" + mySplit[1] + "\n" + mySplit[2];

                    //Place time in orderArray - use the contents of orderArray to order the a day's events
                    Integer orderInput;
                    if(mySplit[0].equals(""))   //Blank file - no time to take from the split
                        orderInput = 0;
                    else
                    {
                        //mySplit[0] example: 14:59
                        String[] myTimeSplit = mySplit[0].split(":");
                        orderInput = Integer.valueOf(myTimeSplit[0] + myTimeSplit[1]);
                        //e.g. orderInput --> 1459
                    }

                    Boolean added = false;
                    int k = 0;
                    while(!added && k < orderArray.size() )
                    {
                        if(orderInput < orderArray.get(k)) {
                            orderArray.add(k, orderInput);  //Add the time to the orderArray, at a certain position
                            added = true;

                            int slot = indexOffset + k;

                            eventArray.add(slot, arrayInput);  //Add the event at the corresponding position in the eventArray
                            arrayInput = "";
                        }
                        k++;
                    }

                    if(!added)  //The list was blank - this was the first entry
                    {
                        orderArray.add(orderInput);
                        eventArray.add(arrayInput);
                        arrayInput = "";
                    }

                }
            }
        }

        //Set the timetable to display its contents
        ArrayAdapter<String> myAdapter=new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, eventArray);
        ListView myList=(ListView) findViewById(R.id.listView);
        myList.setOnItemClickListener(new ListClickHandler());
        myList.setAdapter(myAdapter);

        if(noEvents)
        {
            TextView noEvents_textView = (TextView) findViewById(R.id.noEventsSaved_textView);
            noEvents_textView.setText(getText(R.string.noEventsSaved_month));
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

    //Takes the user back to the daily timetable
    public void viewDayButton(View view)
    {
        Intent intent = new Intent(this, ViewTimetable.class);
        String extraString = date + "END" + dayOfWeek + "END" + "viewingEvents";   //The month view only displays events
        intent.putExtra(MESSAGE_KEY, extraString);
        startActivity(intent);
        finish();
    }

    //Takes the user to next month's timetable
    public void viewNextMonthButton(View view) {
        String[] mySplit = month.split("-"); //Sample contents of month: 12-2015

        int nextMonth = Integer.valueOf(mySplit[0]) + 1;
        int year = Integer.valueOf(mySplit[1]);

        if (nextMonth == 13) {
            nextMonth = 1;
            year = year + 1;
        }
        month = String.valueOf(nextMonth) + "-" + String.valueOf(year); //e.g. 1-2016

        date = "1-" + month;    //e.g. 1-1-2016

        Intent intent = new Intent(this, viewMonth.class);
        String extraString = date + "END" + dayOfWeek + "END" + "viewingEvents";   //The month view only displays events
        intent.putExtra(MESSAGE_KEY, extraString);
        startActivity(intent);
        finish();
    }

    //Takes the user to the previous month's timetable
    public void viewPreviousMonthButton(View view) {
        String[] mySplit = month.split("-"); //Sample contents of month: 1-2016

        int previousMonth = Integer.valueOf(mySplit[0]) - 1;
        int year = Integer.valueOf(mySplit[1]);

        if (previousMonth == 0) {
            previousMonth = 12;
            year = year - 1;
        }
        month = String.valueOf(previousMonth) + "-" + String.valueOf(year); //e.g. 12-2015

        date = "1-" + month;    //e.g. 1-1-2016

        Intent intent = new Intent(this, viewMonth.class);
        String extraString = date + "END" + dayOfWeek + "END" + "viewingEvents";   //The month view only displays events
        intent.putExtra(MESSAGE_KEY, extraString);
        startActivity(intent);
        finish();
    }

    //Takes the user back to a particular day's timetable - used for when a date in the list is clicked on
    public void viewTimetable(String selected_date)
    {
        //Sample message: 10-12-2015ENDThursdayENDviewingEvents
        String extra = selected_date + "END" + dayOfWeek + "END" + "viewingEvents";
        Intent intent = new Intent(this, ViewTimetable.class);
        intent.putExtra(MESSAGE_KEY, extra);
        startActivity(intent);
        finish();
    }

}
