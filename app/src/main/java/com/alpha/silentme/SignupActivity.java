package com.alpha.silentme;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.alpha.silentme.bean.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class SignupActivity extends AppCompatActivity {


    private final String[] spinnerValues = {"Lambton College", "York University", "Humber College"};

    String valCollege="";
    Spinner spinner;
    EditText edtEmail,edtName,edtPassword;
    Button btnSignup,btnLogin;
    private FirebaseAuth firebaseAuth;
    private DatabaseReference usersReference;

    public SignupActivity() {
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        initVars();
    }

    private void initVars() {

        spinner = findViewById(R.id.spinnerColleges);
        edtEmail = findViewById(R.id.edtEmail);
        edtName = findViewById(R.id.edtName);
        edtPassword = findViewById(R.id.edtPassword);
        btnSignup = findViewById(R.id.btnSignup);
        btnLogin = findViewById(R.id.btnLogin);
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(SignupActivity.this,LoginActivity.class));
            }
        });

        firebaseAuth = FirebaseAuth.getInstance();
        usersReference = FirebaseDatabase.getInstance().getReference().child("users");


        btnSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (validations())
                {
                    //Firebase Signup User\
                    signupUser();
                }
            }
        });

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(
                this,
                R.layout.spinner_item, // Custom item layout
                spinnerValues
        );

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        spinner.setAdapter(adapter);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                String selectedValue = spinnerValues[position];
                valCollege = selectedValue;
                Toast.makeText(SignupActivity.this, "Selected: " + selectedValue, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // Do nothing here
            }
        });
    }

    private void signupUser() {
        final String email = edtEmail.getText().toString().trim();
        final String password = edtPassword.getText().toString();
        final String name = edtName.getText().toString();
        final String college = valCollege;

        firebaseAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        String userId = firebaseAuth.getCurrentUser().getUid();
                        DatabaseReference currentUserReference = usersReference.child(userId);

                        User user = new User(email, name, college);
                        currentUserReference.setValue(user);
                        Toast.makeText(this, "user created successfully", Toast.LENGTH_SHORT).show();
                        finish();
                        finishAffinity();
                        // Perform success actions (e.g., navigate to another activity)
                    } else {

                        Toast.makeText(this, "Error", Toast.LENGTH_SHORT).show();
                        // Handle failure (e.g., display an error message)
                    }
                });
    }

    private boolean validations() {
        return true;
    }
}