package com.appzum.smartshoppinglist;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity {

    TextView statusTextView;
    EditText emailET;
    EditText passwordET;
    Button loginBtn;
    Button registerBtn;

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mAuth = FirebaseAuth.getInstance();

        statusTextView = findViewById(R.id.statusTextView);
        emailET = findViewById(R.id.emailET);
        passwordET = findViewById(R.id.passwordET);
        loginBtn = findViewById(R.id.loginBtn);
        registerBtn = findViewById(R.id.registerBtn);

        loginBtn.setOnClickListener((View v) -> {
            String email = emailET.getText().toString();
            String password = passwordET.getText().toString();
            mAuth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this, task -> {
                        if (task.isSuccessful()) {
                            FirebaseUser user = mAuth.getCurrentUser();
                            switchToNext();
                        } else {
                            Toast.makeText(LoginActivity.this, getString(R.string.authentication_error),
                                    Toast.LENGTH_SHORT).show();
                        }
                    });
        });

        registerBtn.setOnClickListener((View v) -> {
            String email = emailET.getText().toString();
            String password = passwordET.getText().toString();
            if (password.length() < 6) {
                Toast.makeText(this, R.string.password_is_short, Toast.LENGTH_SHORT).show();
                return;
            }
            mAuth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this, task -> {
                        if (task.isSuccessful()) {
                            FirebaseUser user = mAuth.getCurrentUser();
                            switchToNext();
                        } else {
                            Toast.makeText(LoginActivity.this, getString(R.string.authentication_error),
                                    Toast.LENGTH_SHORT).show();
                        }
                    });
        });
    }


    @Override
    public void onStart() {
        super.onStart();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            switchToNext();
        }
    }

    void switchToNext() {
        SharedPreferences sPref = getSharedPreferences("family", MODE_PRIVATE);
        String family_name = sPref.getString("family_name", "none");
        if (!family_name.equals("none")) {
            Intent intent = new Intent(this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
        } else {
            Intent intent = new Intent(this, ChooseFamilyActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
        }
    }

}