package ie.tcd.scss.amhiggin.collegecalendar;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import java.io.IOException;
import java.io.OutputStreamWriter;

public class AddToDoItem extends AppCompatActivity {

    public final static String MESSAGE_KEY = "ie.tcd.scss.amhiggin.collegecalendar";

    String itemFileName = "myToDoList";

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_to_do_item);

        /*ArrayAdapter<String> stringArrayAdapter1 = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, validPriorityArray);
        Spinner prioritySpinner = (Spinner) findViewById(R.id.prioritySpinner);
        prioritySpinner.setAdapter(stringArrayAdapter1);*/

    }

    //Implements the saveEventButton - saves the event data and ends the AddNewEvent activity
    public void saveItemButton(View view) {

        String item_title, item_priority, item_deadline, item_complete;

        EditText titleBox = (EditText) findViewById(R.id.titleBox);

        //Get the input entered by the user
        item_title = titleBox.getText().toString();
        //Spinner spinner1 =(Spinner)findViewById(R.id.prioritySpinner);

        //item_priority = spinner1.getSelectedItem().toString();
        if(item_title.equals(""))
            item_title = getString(R.string.noItemsSaved);
        item_complete = item_title + "END_OF_ITEM";

        //Use the entered time to determine which event file to save the event to
        itemFileName = itemFileName + ".txt";

        writeToFile(item_complete, itemFileName);    //Save the entered values to the event file
        shortToast( getString(R.string.itemSaved) );

        //Return to the ViewNotes activity
        Intent intent = new Intent(this, ToDoList.class);
        startActivity(intent);
        finish();   //Close the AddNewNote activity

    }

    //Saves the specified "data" string to the file specified when the AddNewEvent activity started
    private void writeToFile(String data, String fileName) {

        try {
            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(openFileOutput(fileName, Context.MODE_APPEND));//Context.MODE_PRIVATE));
            //Note - changing the context to Context.MODE_APPEND adds to the end of the file, rather than overwriting it
            outputStreamWriter.write(data);
            outputStreamWriter.close();
        }
        catch (IOException e) //Won't compile without this method - can put in some debugging messages
        {
        }
    }

    //Implements the cancel button - ends the AddToDoItem activity
    public void cancelButton(View view) {

        //Return to the ToDoList activity
        Intent intent = new Intent(this, ToDoList.class);
        startActivity(intent);
        finish();   //Close the AddToDoItem activity, and return to the ToDoList activity
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

}