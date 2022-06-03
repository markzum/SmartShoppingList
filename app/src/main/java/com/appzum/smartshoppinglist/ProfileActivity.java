package com.appzum.smartshoppinglist;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class ProfileActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;

    Button backFromProfileBtn;
    TextView userEmailTV;
    TextView familyNameTV;
    TextView familyPasswordTV;
    Button singOutBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();

        if (currentUser == null) {
            return;
        }

        SharedPreferences sPref = getSharedPreferences("family", MODE_PRIVATE);
        String family_name = sPref.getString("family_name", "none");
        String family_password = sPref.getString("family_password", "none");

        backFromProfileBtn = findViewById(R.id.backFromProfileBtn);
        userEmailTV = findViewById(R.id.userEmailTV);
        familyNameTV = findViewById(R.id.familyNameTV);
        familyPasswordTV = findViewById(R.id.familyPasswordTV);
        singOutBtn = findViewById(R.id.singOutBtn);

        userEmailTV.setText(currentUser.getEmail());
        familyNameTV.setText(getString(R.string.it_is_name_of_family) + "\n" + family_name);
        familyPasswordTV.setText(getString(R.string.it_is_password_of_family) + "\n" + family_password);

        backFromProfileBtn.setOnClickListener((View v) -> {
            Intent i = new Intent(this, MainActivity.class);
            i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(i);
        });

        singOutBtn.setOnClickListener((View v) -> {
            new AlertDialog.Builder(this)
                    //.setTitle("")
                    .setMessage(R.string.are_you_sure_you_want_log_out)
                    .setPositiveButton(android.R.string.yes, (dialog, which) -> {
                        SharedPreferences.Editor sPrefEdit = sPref.edit();
                        sPrefEdit.putString("family_name", "none");
                        sPrefEdit.putString("family_password", "none");
                        sPrefEdit.putString("is_first_launch", "true");
                        sPrefEdit.putString("is_created", "false");
                        sPrefEdit.apply();
                        mAuth.signOut();
                        Intent intent = new Intent(ProfileActivity.this, LoginActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);
                    }).setNegativeButton(android.R.string.no, null)
                    .show();



        });

    }


    public void onStart() {
        super.onStart();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser == null) {
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
        }
    }
}