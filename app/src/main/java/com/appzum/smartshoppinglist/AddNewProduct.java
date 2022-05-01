package com.appzum.smartshoppinglist;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

public class AddNewProduct extends AppCompatActivity {
    EditText nameEditText;
    EditText descriptionEditText;
    Button okayBtn;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_new_product);

        nameEditText = (EditText) findViewById(R.id.nameEditText);
        descriptionEditText = (EditText) findViewById(R.id.descriptionEditText);
        okayBtn = (Button) findViewById(R.id.okayBtn);

        okayBtn.setOnClickListener(view -> {
            Intent intent = new Intent();
            intent.putExtra("name", nameEditText.getText().toString());
            intent.putExtra("description", descriptionEditText.getText().toString());
            setResult(RESULT_OK, intent);
            finish();
        });
    }
}
