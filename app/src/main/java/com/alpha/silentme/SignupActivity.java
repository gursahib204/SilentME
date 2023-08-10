package com.alpha.silentme;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.alpha.silentme.bean.User;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class SignupActivity extends AppCompatActivity {

    private final String[] spinnerValues = {"Lambton College", "York University", "Humber College"};

    String valCollege="";
    Spinner spinner;
    EditText edtEmail,edtName,edtPassword;
    Button btnSignup,btnLogin;
    private FirebaseAuth firebaseAuth;
    private DatabaseReference usersReference;
    private ImageView imgProfilePicture;
    private Uri selectedImageUri; // Store the selected image URI

    private static final int PICK_IMAGE_REQUEST = 1;
    SharedPreferences sharedPreferences;


    public SignupActivity() {
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        FirebaseApp.initializeApp(this);


        initVarsSignup();

        if (sharedPreferences.getBoolean("isLoggedIn", false)) {
            // User is already logged in, navigate to DashboardActivity
            startActivity(new Intent(SignupActivity.this, DashboardActivity.class));
            finish(); // Optionally finish the login activity
        }
    }

    private void initVarsSignup() {

        spinner = findViewById(R.id.spinnerColleges);
        edtEmail = findViewById(R.id.edtEmail);
        edtName = findViewById(R.id.edtName);
        edtPassword = findViewById(R.id.edtPassword);
        btnSignup = findViewById(R.id.btnSignup);
        btnLogin = findViewById(R.id.btnLogin);
        imgProfilePicture = findViewById(R.id.imgProfilePicture);
        Button btnChoosePicture = findViewById(R.id.btnChoosePicture);

        sharedPreferences = getSharedPreferences("session", MODE_PRIVATE);

        btnChoosePicture.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(intent, PICK_IMAGE_REQUEST);
        });

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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null) {
            selectedImageUri = data.getData();
            imgProfilePicture.setImageURI(selectedImageUri);
        }
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

                        // Upload the selected image to Firebase Storage
                        StorageReference storageRef = FirebaseStorage.getInstance().getReference();
                        StorageReference profilePictureRef = storageRef.child("profile_pictures/" + userId);
                        profilePictureRef.putFile(selectedImageUri)
                                .addOnSuccessListener(taskSnapshot -> {
                                    profilePictureRef.getDownloadUrl().addOnSuccessListener(uri -> {
                                        String profilePictureUrl = uri.toString(); // Set the profile picture URL

                                        // Create a new user object with all the data including profilePictureUrl
//                                        User user = new User(name, email, college, profilePictureUrl,);

                                        // Save the user object to the Realtime Database
                                        DatabaseReference currentUserReference = usersReference.child(userId);
//                                        currentUserReference.setValue(user);


                                        Toast.makeText(this, "User created successfully", Toast.LENGTH_SHORT).show();
                                        Intent intent = new Intent(this, DashboardActivity.class);
                                        startActivity(intent);
                                        finish();
                                    });
                                })
                                .addOnFailureListener(exception -> {
                                    Toast.makeText(this, "Failed to upload profile picture", Toast.LENGTH_SHORT).show();
                                });
                    } else {
                        String errorMessage = task.getException().getMessage();
                        Toast.makeText(this, "Error creating user: " + errorMessage, Toast.LENGTH_SHORT).show();
                    }
                });
    }


    private boolean validations() {
        if (edtEmail.getText().toString().isEmpty()) {
            Toast.makeText(this, "Please enter email", Toast.LENGTH_SHORT).show();
            return false;
        } else if (edtPassword.getText().toString().isEmpty()) {
            Toast.makeText(this, "Please enter Password", Toast.LENGTH_SHORT).show();
            return false;
        } else {
            return true;
        }
    }





}