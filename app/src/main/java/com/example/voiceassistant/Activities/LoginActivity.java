package com.example.voiceassistant.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import com.example.voiceassistant.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;

    private static final String PREF_FILE_NAME = "PhoneFixPrefs";
    private static final String KEY_EMAIL = "email";
    private static final String KEY_PASSWORD = "password";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mAuth = FirebaseAuth.getInstance();

        EditText emailEditText = findViewById(R.id.email_edit_text);
        EditText passwordEditText = findViewById(R.id.password_edit_text);

        MaterialButton loginButton = findViewById(R.id.login_button);
        CheckBox rememberMeCheckBox = findViewById(R.id.remember_me_check_box);

        SharedPreferences preferences = getSharedPreferences(PREF_FILE_NAME, Context.MODE_PRIVATE);
        String savedEmail = preferences.getString(KEY_EMAIL, "");
        String savedPassword = preferences.getString(KEY_PASSWORD, "");

        if (!savedEmail.isEmpty() && !savedPassword.isEmpty() && rememberMeCheckBox.isChecked()) {
            emailEditText.setText(savedEmail);
            passwordEditText.setText(savedPassword);
            loginUser(savedEmail, savedPassword);
        }

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = emailEditText.getText().toString();
                String password = passwordEditText.getText().toString();

                if (rememberMeCheckBox.isChecked()) {
                    SharedPreferences preferences = getSharedPreferences(PREF_FILE_NAME, Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = preferences.edit();
                    editor.putString(KEY_EMAIL, email);
                    editor.putString(KEY_PASSWORD, password);
                    editor.apply();
                }

                mAuth.signInWithEmailAndPassword(email, password)
                        .addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                                    startActivity(intent);
                                    finish();
                                } else {
                                    Toast.makeText(LoginActivity.this, "Авторизация не удалась. Попробуйте еще раз.", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
            }
        });

        mAuth.addAuthStateListener(new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                    startActivity(intent);
                    finish();
                }
            }
        });
    }

    private void loginUser(String email, String password) {
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                            startActivity(intent);
                            finish();
                        } else {
                            Toast.makeText(LoginActivity.this, "Авторизация не удалась. Попробуйте еще раз.", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
}