package com.example.simpletodo;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    public static final String KEY_ITEM_TEXT = "item_text";
    public static final String KEY_ITEM_POSITION = "item_position";
    public static final int EDIT_TEXT_CODE = 20;
    List<String> items; // strings containing tems to-do
    Button btnAdd; // object used to add a new to-do item
    EditText etItem;
    RecyclerView rvItems;
    ItemsAdapter itemsAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // initializing views
        btnAdd = findViewById(R.id.btnAdd);
        etItem = findViewById(R.id.etItem);
        rvItems = findViewById(R.id.rvItmes);

        loadItems();

        ItemsAdapter.OnLongClickListener onLongClickListener = new ItemsAdapter.OnLongClickListener(){
            @Override
            public void onItemLongClicked(int position){
                // Delete the item from the model
                items.remove(position);
                // Notify adapter that item was deleted
                itemsAdapter.notifyItemRemoved(position);
                // Feedback to use about removal
                Toast.makeText(getApplicationContext(),"Item was removed",Toast.LENGTH_SHORT).show();
                // Save changes
                saveItems();

            }
        };

        ItemsAdapter.OnClickListener onClickListener = new ItemsAdapter.OnClickListener() {
            @Override
            public void onItemClicked(int position) {
                Log.d("MainActivity", "Single click at position " + position);
                // Create new activity
                Intent i = new Intent(MainActivity.this, EditActivity.class);
                // Pass data being edited to new activity
                i.putExtra(KEY_ITEM_TEXT, items.get(position));
                i.putExtra(KEY_ITEM_POSITION, position);
                // Display activity
                startActivityForResult(i,EDIT_TEXT_CODE);
                /* startActivityForResult appears to be depricated.
                *  todo find out what would be the most up to date method and replace
                */
            }
        };
        itemsAdapter = new ItemsAdapter(items,onLongClickListener,onClickListener);
        rvItems.setAdapter(itemsAdapter);
        rvItems.setLayoutManager(new LinearLayoutManager(this));

        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String todoItem = etItem.getText().toString();
                // Add item to model
                items.add(todoItem);
                // Notify adapter that item was inserted
                itemsAdapter.notifyItemInserted(items.size()-1);
                etItem.setText(""); // clearing the text box, because text has been submitted
                // giving the user feedback that item was added
                Toast.makeText(getApplicationContext(),"Item was added",Toast.LENGTH_SHORT).show();
                // Save added item
                saveItems();
            }
        });
    }

    // This function handles the result of the edit activity
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if(resultCode == RESULT_OK && requestCode == EDIT_TEXT_CODE){
            // Retrieve the updated text value
            String itemText = data.getStringExtra(KEY_ITEM_TEXT);
            // Extract the original position of the edit item from key position
            int position = data.getExtras().getInt(KEY_ITEM_POSITION);

            // Update the model at the position with new item
            items.set(position, itemText);

            // Notify the adapter
            itemsAdapter.notifyItemChanged(position);

            // Persist the changes
            saveItems();

            // User feedback
            Toast.makeText(getApplicationContext(),"Item updated successfully",Toast.LENGTH_SHORT).show();

        } else{
            Log.w("MainActivity", "Uknown call to onActivityResult");
        }
        /* Android studio wants a call to super to be made, even though
        * the method is being edited.
        * */
        // super.onActivityResult(requestCode, resultCode, data);
    }

    // This function returns the file to operate on
    private File getDataFile() {
        return new File(getFilesDir(), "data.txt");
    }

    // This function will read the file (to get the items)
    private void loadItems(){
        try {
            items = new ArrayList<>(FileUtils.readLines(getDataFile(), Charset.defaultCharset()));
        } catch (IOException e) { // in case items initializaion fails
            // Log
            Log.e("MainActivity", "Error reading items", e);
            // Initialize items to an empty list
            items = new ArrayList<>();
        }
    }

    // This function will write on the file (to save the items)
    private void saveItems(){
        try {
            FileUtils.writeLines(getDataFile(),items);
        } catch (IOException e) {
            Log.e("MainActivity", "Error writting items", e);
        }
    }

}