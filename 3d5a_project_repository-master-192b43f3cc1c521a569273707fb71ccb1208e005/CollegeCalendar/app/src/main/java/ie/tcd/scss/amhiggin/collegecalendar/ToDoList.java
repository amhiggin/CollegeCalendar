package ie.tcd.scss.amhiggin.collegecalendar;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.View;
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
import java.util.ArrayList;

public class ToDoList extends ActionBarActivity {

    public final static String MESSAGE_KEY = "ie.tcd.scss.amhiggin.collegecalendar";

    String title = "myToDoList";
    ArrayList<String> itemArray2 = new ArrayList<String>();
    ArrayList<String> itemArray = new ArrayList<String>();
    ArrayList<String> selectedItems = new ArrayList<String>();
    Boolean itemsOnFile = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_to_do_list);

        TextView viewTitle = (TextView) findViewById(R.id.viewTitle);
        populateToDoList(title);
        viewTitle.setText(getString(R.string.items));

        if (itemsOnFile) {
            //N.B. This ArrayAdapter code needs to be placed AFTER setContentView(R..layout.activity_to_do_list);
            ArrayAdapter<String> myAdapter = new ArrayAdapter<String>(this, R.layout.list_item, R.id.listText, itemArray);
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

        ArrayAdapter<String> myAdapter = new ArrayAdapter<String>(this, R.layout.list_checkbox_item, R.id.listText, itemArray);
        myList.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
        myList.setAdapter(myAdapter);

        //Make the "Cancel Deletion" button appear
        Button cancelDeletionButton = (Button) findViewById(R.id.cancelDeletionButton);
        cancelDeletionButton.setVisibility(View.VISIBLE);

        //Make the "Delete" button appear
        Button deleteButton = (Button) findViewById(R.id.deleteButton);
        deleteButton.setVisibility(View.VISIBLE);

        //Make the "Add New Item" button disappear
        Button addNewItemButton = (Button) findViewById(R.id.addNewItemButton);
        addNewItemButton.setVisibility(View.GONE);

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

        //Make the "Add New Item" button appear
        Button addNewItemButton = (Button) findViewById(R.id.addNewItemButton);
        addNewItemButton.setVisibility(View.VISIBLE);

        //Make the "Cancel Deletion" button disappear
        Button cancelDeletionButton = (Button) findViewById(R.id.cancelDeletionButton);
        cancelDeletionButton.setVisibility(View.GONE);

        //Make the "Delete" button disappear
        Button deleteButton = (Button) findViewById(R.id.deleteButton);
        deleteButton.setVisibility(View.GONE);

        ArrayAdapter<String> myAdapter = new ArrayAdapter<String>(this, R.layout.list_item, R.id.listText, itemArray);
        myList.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
        myList.setAdapter(myAdapter);
    }

    //Implements editing of items
    public class ListClickHandler implements AdapterView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView parent, View view, int position, long id) {
            TextView listItem = (TextView) view.findViewById(R.id.listText);
            String text = listItem.getText().toString();
            String click = "";
            click = click + text;

            if(!click.equals(""))
                editToDoItem(click);
        }
    }

    //Implements selection of multiple list elements for deletion
    public class checkBoxClickHandler implements AdapterView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView parent, View view, int position, long id) {
            TextView listItem = (TextView) view.findViewById(R.id.listText);
            String text = listItem.getText().toString();

            String click = "";
            click = click + text + "END_OF_ITEM";

            if (view != null) {
                CheckBox checkBox = (CheckBox) view.findViewById(R.id.checkBox);
                checkBox.setChecked(!checkBox.isChecked());
                if (checkBox.isChecked()) {
                    addToSelectedItems(click);
                } else {
                    removeFromSelectedItems(click);
                }
            }
        }
    }

    //Add the string from an item to the selected items array
    public void addToSelectedItems(String addition) {
        Boolean duplicate = false;
        for (int i = 0; i < selectedItems.size(); i++) {
            if (addition.equals(selectedItems.get(i)))
                duplicate = true;
        }
        if (!duplicate) ;
        selectedItems.add(addition);

    }

    //Remove the string from an item from the selected items array
    public void removeFromSelectedItems(String removal) {
        for (int i = 0; i < selectedItems.size(); i++) {
            if (removal.equals(selectedItems.get(i)))
                selectedItems.remove(i);
        }
    }

    public void cancelDeletion(View view)
    {
        revertClicker();
        selectedItems.clear();
        Button cancelDeletionButton = (Button) findViewById(R.id.cancelDeletionButton);
        cancelDeletionButton.setVisibility(View.GONE);

        Button deleteButton = (Button) findViewById(R.id.deleteButton);
        deleteButton.setVisibility(View.GONE);
    }

    public void deleteSelectedItems()
    {
        populateToDoList_noLineBreaks(title);

        //The selectedItems ArrayList contains the strings matching the items to be deleted

        //Loop through all of the selectedItems
        for(int selectedItems_i = 0; selectedItems_i < selectedItems.size(); selectedItems_i++)
        {
            //Loop through the itemArray for each item in selectedItems
            for(int itemArray_i = 0; itemArray_i < itemArray2.size(); itemArray_i++)
            {
                if(selectedItems.get(selectedItems_i).equals(itemArray2.get(itemArray_i))) {
                    //This item in itemArray matches one of the selected items
                    itemArray2.remove(itemArray_i);
                }
            }
        }

        //Now, loop through the array, and take all the text from it
        String allArrayInput = "";

        for(int i = 0; i < itemArray2.size(); i++)
        {
            allArrayInput = allArrayInput + itemArray2.get(i);
        }

        String itemFileName;
        itemFileName = title + ".txt";

        writeToFile_Overwrite(allArrayInput, itemFileName);    //Save the entered values to file
        shortToast(getString(R.string.itemDeleted));

        //If this was the only item in the timetable, delete the file itself
        if(itemArray2.size() == 0)
            deleteItem(itemFileName);


        selectedItems.clear();
        //itemArray2.clear();

    }

    //Populates the contents of the list to match saved values (if any exist) for the selected day
    public void populateToDoList(final String selected_title) {
        String itemFileName;
        itemFileName = selected_title + ".txt";

        String myString = readFromFile(itemFileName);  //Search the internal storage for data corresponding to itemFileName.txt
        if (myString.equals("")) //No items for this file
        {
            itemsOnFile = false;
            TextView noItems_textView = (TextView) findViewById(R.id.noItemsSaved_textView);
            noItems_textView.setText(getText(R.string.noItemsSaved));
        }
        String arrayInput = "";
        String[] myOtherSplit = myString.split("END_OF_ITEM");

        for (int i = 0; i < myOtherSplit.length; i++) {
            arrayInput = myOtherSplit[i];
            itemArray.add(i, arrayInput);
        }

        if (itemArray.size() != 1) {
            //Set the list to display its contents
            //ArrayAdapter<String> myAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, itemArray);
            ArrayAdapter<String> myAdapter = new ArrayAdapter<String>(this, R.layout.list_item, R.id.listText, itemArray);
            ListView myList = (ListView) findViewById(R.id.listView);
            myList.setOnItemClickListener(new ListClickHandler());
            myList.setAdapter(myAdapter);
        }
    }

    //Reads values from the saved file. Used in populateToDoList()
    private String readFromFile(String itemFileName)
    {
        String inputFromFile = "";
        try {
            InputStream myInputStream = openFileInput(itemFileName);

            if (myInputStream != null)
            {
                InputStreamReader myInputStreamReader = new InputStreamReader(myInputStream);
                BufferedReader myBufferedReader = new BufferedReader(myInputStreamReader);
                StringBuilder myStringBuilder = new StringBuilder();
                String myInputString = myBufferedReader.readLine();

                while (myInputString != null)
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

    //Implements the "Delete all" button, which deletes all items for the day, clearing the list
    public void deleteButton(View view) {

        revertClicker();
        deleteSelectedItems();

        //Restart the activity
        Intent intent = getIntent();
        startActivity(intent);
        finish();

    }

    //Implements the addNewItemButton, which adds a new item to the list
    public void addNewItemButton(View view) {
        Intent intent = new Intent(this, AddToDoItem.class);
        startActivity(intent);
        finish();
    }

    //Deletes an item's file and updates the list to account for any changes
    public void deleteItem (String myItemFileName)
    {
        //Select the file to be deleted
        File directory = getFilesDir();
        File file = new File(directory, myItemFileName);

        if(!file.exists()) //If the file does not exist
        {
            //Do nothing
        }
        else    //If the file exists
        {
            file.delete();  //Delete the file

            if (!file.exists())   //Check that the file has been successfully deleted
                populateToDoList(title);
            else    //File was not deleted - an error occured
                shortToast(getString(R.string.error_fileNotDeleted));
        }
    }

    //Display a short Toast message
    public void shortToast(String message) {
        Context context = getApplicationContext();
        CharSequence text = message;
        int duration = Toast.LENGTH_SHORT;
        Toast toast = Toast.makeText(context, text, duration);
        toast.show();
    }

    //Takes the user to the editToDoItem activity
    public void editToDoItem(String message)
    {
        Intent intent = new Intent(this, EditToDoItem.class);
        //Sample message: notes
        intent.putExtra(MESSAGE_KEY, message);
        startActivity(intent);
        finish();
    }


    //Populates the contents of the list to match saved values (if any exist) for the selected day - used for deletion of selected items
    public void populateToDoList_noLineBreaks(final String selected_title) {
        String itemFileName;

        itemFileName = selected_title + ".txt";

        String myString = readFromFile(itemFileName);  //Search the internal storage for data corresponding to itemFileName.txt
        String arrayInput = "";

        String[] myOtherSplit = myString.split("END_OF_ITEM"); //Split into separate notes

        for (int i = 0; i < myOtherSplit.length; i++) {
            arrayInput = myOtherSplit[i] + "END_OF_ITEM";   //e.g. arrayInput = notesEND_OF_ITEM
            itemArray2.add(arrayInput);
        }

    }

    //Saves the specified "data" string to the file specified when the editNote activity started
    private void writeToFile_Overwrite(String data, String itemFileName) {

        try {
            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(openFileOutput(itemFileName, Context.MODE_PRIVATE));//Context.MODE_APPEND));
            //Note - changing the context to Context.MODE_APPEND adds to the end of the file, rather than overwriting it
            outputStreamWriter.write(data);
            outputStreamWriter.close();
        } catch (IOException e) //Won't compile without this method - can put in some debugging messages
        {
        }
    }
}