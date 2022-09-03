package com.appzum.smartshoppinglist;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


public class ChooseFamilyActivity extends AppCompatActivity {

    private static final String TAG = "markzum";
    EditText familyNameJoinET;
    EditText familyPasswordJoinET;
    Button joinFamilyBtn;
    EditText familyNameCreateET;
    Button createFamilyBtn;
    private DatabaseReference mDatabase;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_family);

        mAuth = FirebaseAuth.getInstance();

        EditText familyNameJoinET = findViewById(R.id.familyNameJoinET);
        EditText familyPasswordJoinET = findViewById(R.id.familyPasswordJoinET);
        Button joinFamilyBtn = findViewById(R.id.joinFamilyBtn);
        EditText familyNameCreateET = findViewById(R.id.familyNameCreateET);
        Button createFamilyBtn = findViewById(R.id.createFamilyBtn);

        joinFamilyBtn.setOnClickListener((View v) -> {
            String familyName = familyNameJoinET.getText().toString();
            String familyPassword = familyPasswordJoinET.getText().toString();
            FirebaseUser currentUser = mAuth.getCurrentUser();

            mDatabase = FirebaseDatabase.getInstance().getReference("smartShoppingList")
                    .child("families").child(familyName).child(familyPassword);

            mDatabase.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (!dataSnapshot.exists()) {
                        new AlertDialog.Builder(ChooseFamilyActivity.this)
                                .setMessage(R.string.family_name_or_password_error)
                                .setPositiveButton(android.R.string.ok, null)
                                .show();
                    } else {
                        SharedPreferences sPref = getSharedPreferences("family", MODE_PRIVATE);
                        SharedPreferences.Editor sPrefEdit = sPref.edit();
                        sPrefEdit.putString("family_name", familyName);
                        sPrefEdit.putString("family_password", familyPassword);
                        sPrefEdit.apply();

                        mDatabase.child("users").child(currentUser.getUid()).setValue(currentUser.getEmail());

                        Intent intent = new Intent(ChooseFamilyActivity.this, MainActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);

                        mDatabase.removeEventListener(this);
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    Log.w(TAG, "loadPost:onCancelled", databaseError.toException());
                }
            });
        });


        createFamilyBtn.setOnClickListener((View v) -> {
            String familyName = familyNameCreateET.getText().toString();
            String familyPassword = CreateId.createFamilyPassword();
            String debug_product_id = CreateId.createId();
            FirebaseUser currentUser = mAuth.getCurrentUser();

            mDatabase = FirebaseDatabase.getInstance().getReference("smartShoppingList")
                    .child("families").child(familyName);


            mDatabase.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        new AlertDialog.Builder(ChooseFamilyActivity.this)
                                .setMessage(R.string.this_family_already_exists)
                                .setPositiveButton(android.R.string.ok, null)
                                .show();
                    } else {
                        mDatabase.child(familyPassword)
                                .child("products").child(debug_product_id)
                                .setValue(new Product(debug_product_id,
                                        "DebugProduct",
                                        "DebugProduct",
                                        "debug",
                                        "01.01.2022 12:00",
                                        "01.01.2022 12:00",
                                        "admin",
                                        "false"));

                        mDatabase.child(familyPassword)
                                .child("users").child(currentUser.getUid()).setValue(currentUser.getEmail());

                        SharedPreferences sPref = getSharedPreferences("family", MODE_PRIVATE);
                        SharedPreferences.Editor sPrefEdit = sPref.edit();
                        sPrefEdit.putString("family_name", familyName);
                        sPrefEdit.putString("family_password", familyPassword);
                        sPrefEdit.putString("is_created", "true");
                        sPrefEdit.apply();

                        Intent intent = new Intent(ChooseFamilyActivity.this, MainActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);

                        mDatabase.removeEventListener(this);
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    Log.w(TAG, "loadPost:onCancelled", databaseError.toException());
                }
            });
        });
    }
}