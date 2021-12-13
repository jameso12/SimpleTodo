package com.example.simpletodo;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class EditActivity extends AppCompatActivity {
    EditText updtItem;
    Button btnSave;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);

        updtItem = findViewById(R.id.updtItem);
        btnSave = findViewById(R.id.btnSave);

        getSupportActionBar().setTitle("Edit item");
        updtItem.setText(getIntent().getStringExtra(MainActivity.KEY_ITEM_TEXT));

        // Saves changes when user is done editing text and clicks button
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // create an intent which contains the result
                Intent intent = new Intent();

                // pass the data to main intent
                intent.putExtra(MainActivity.KEY_ITEM_TEXT, updtItem.getText().toString());
                intent.putExtra(MainActivity.KEY_ITEM_POSITION, getIntent().getExtras().getInt(MainActivity.KEY_ITEM_POSITION));

                // set the results
                setResult(RESULT_OK, intent);

                // finish the activity, to go back to previous screen
                finish();
            }
        });
    }
}