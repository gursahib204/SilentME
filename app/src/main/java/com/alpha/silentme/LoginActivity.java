package com.alpha.silentme;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.animation.Animator;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.animation.DecelerateInterpolator;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Toast;

import com.alpha.silentme.bean.User;
import com.google.android.gms.maps.model.Dash;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class LoginActivity extends AppCompatActivity {
    EditText edtEmail,edtPassword;
    private FirebaseAuth firebaseAuth;
    Button btnLogin;
    private FloatingActionButton fab;
    private LinearLayout login_page_login_page,login_page_register_page;
    SharedPreferences sharedPreferences;

    //Signup Variables
    private final String[] spinnerValues = {"Lambton College", "York University", "Humber College"};

    String valCollege="";
    Spinner spinner;
    EditText edtEmailSignup,edtName,edtPasswordSignup,edtConfirmPasswordSignup;
    Button btnSignup;
    private DatabaseReference usersReference;
    private ImageView imgProfilePicture;
    private Uri selectedImageUri; // Store the selected image URI

    private static final int PICK_IMAGE_REQUEST = 1;
    private String profilePictureUrl="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        initVars();
        initVarsSignup();
    }

    private void initVarsSignup() {

        spinner = findViewById(R.id.spinnerColleges);
        edtEmailSignup = findViewById(R.id.edtEmailSignup);
        edtName = findViewById(R.id.edtNameSignup);
        edtPasswordSignup = findViewById(R.id.edtPasswordSignup);
        edtConfirmPasswordSignup = findViewById(R.id.edtConfirmPassword);
        btnSignup = findViewById(R.id.btnRegister);
        imgProfilePicture = findViewById(R.id.imgProfilePicture);
        Button btnChoosePicture = findViewById(R.id.btnChoosePicture1);

        sharedPreferences = getSharedPreferences("session", MODE_PRIVATE);

        btnChoosePicture.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(intent, PICK_IMAGE_REQUEST);
        });


        firebaseAuth = FirebaseAuth.getInstance();
        usersReference = FirebaseDatabase.getInstance().getReference().child("users");


        btnSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (validationsSignup())
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
                Toast.makeText(LoginActivity.this, "Selected: " + selectedValue, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // Do nothing here
            }
        });
    }

    private boolean validationsSignup() {
        if (profilePictureUrl.isEmpty())
        {
            Toast.makeText(this, "Please Select Image", Toast.LENGTH_SHORT).show();
            return false;
        }
        else if (edtName.getText().toString().isEmpty())
        {
            Toast.makeText(this, "Please enter Name", Toast.LENGTH_SHORT).show();
            return false;
        }
       else if (edtEmailSignup.getText().toString().trim().isEmpty()) {
        Toast.makeText(this, "Please enter email", Toast.LENGTH_SHORT).show();
        return false;
       }

        else if (edtPasswordSignup.getText().toString().isEmpty())
        {
            Toast.makeText(this, "Please enter Password", Toast.LENGTH_SHORT).show();
            return false;
        }
        else if (!(edtPasswordSignup.getText().toString().equals(edtConfirmPasswordSignup.getText().toString())))
        {
            Toast.makeText(this, "Password mismatch", Toast.LENGTH_SHORT).show();
            return false;
        }
        else {
            return true;
        }

    }

    private void signupUser() {
        final String email = edtEmailSignup.getText().toString().trim();
        Log.d("Debug", "Email: " + email);
        final String password = edtPasswordSignup.getText().toString();
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
                                        String profilePicUrl = uri.toString(); // Set the profile picture URL

                                        // Create a new user object with all the data including profilePictureUrl
                                        User user = new User(name, email, college, profilePicUrl);

                                        // Save the user object to the Realtime Database
                                        DatabaseReference currentUserReference = usersReference.child(userId);
                                        currentUserReference.setValue(user);

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
    private void initVars() {
        //GUI
        login_page_login_page = (LinearLayout)findViewById(R.id.login_page_login_page);
        login_page_register_page = (LinearLayout)findViewById(R.id.login_page_register_page);
        edtEmail =findViewById(R.id.edtEmail);
        edtPassword =findViewById(R.id.edtPassword);
        btnLogin =findViewById(R.id.btnLogin);
        sharedPreferences=getSharedPreferences("session",MODE_PRIVATE);

        String ss=sharedPreferences.getString("email","0");

        // Toast.makeText(this, ""+ss, Toast.LENGTH_SHORT).show();

        if(ss!="0"){

            Intent i = new Intent(getApplicationContext(), DashboardActivity.class);
            startActivity(i);
            finish();

        }

        firebaseAuth = FirebaseAuth.getInstance();

        fabButton();

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (validations())
                {
                    login();
                }
            }
        });
    }

    private boolean loginLogin = true;

    private void fabButton() {
        fab = (FloatingActionButton)findViewById(R.id.login_student_fab_btn);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (loginLogin) {
                    //Open Reg Page

                    Animator circularReveal = ViewAnimationUtils
                            .createCircularReveal(login_page_register_page, (login_page_register_page.getWidth() - 200), 150, 0, (login_page_register_page.getHeight() + 100));
                    circularReveal.setDuration(600);
                    circularReveal.setStartDelay(190);
                    circularReveal.setInterpolator(new DecelerateInterpolator());
                    circularReveal.start();

                    fab.animate().setInterpolator(new DecelerateInterpolator())
                            .setDuration(600).translationY(50)
                            .translationX(-100)
                            .rotation(135).start();

                    Handler hnd = new Handler(Looper.getMainLooper());
                    hnd.postDelayed(new Runnable() {
                        @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
                        @Override
                        public void run() {
                            login_page_login_page.setVisibility(View.INVISIBLE);
                            login_page_register_page.setVisibility(View.VISIBLE);
                            fab.setElevation(0);
                        }
                    }, 200);

                    loginLogin = false;
                } else {
                    login_page_login_page.setVisibility(View.VISIBLE);
                    login_page_register_page.setVisibility(View.INVISIBLE);

                    Animator circularReveal = ViewAnimationUtils
                            .createCircularReveal(login_page_login_page, (login_page_login_page.getWidth() - 200), 150, 0, (login_page_login_page.getHeight() + 100));
                    circularReveal.setDuration(600);
                    circularReveal.setInterpolator(new DecelerateInterpolator());
                    circularReveal.start();

                    fab.animate().setInterpolator(new DecelerateInterpolator())
                            .setDuration(200).translationY(0)
                            .translationX(0)
                            .rotation(0).start();

                    Handler hnd = new Handler(Looper.getMainLooper());
                    hnd.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            login_page_login_page.setVisibility(View.VISIBLE);
                            login_page_register_page.setVisibility(View.INVISIBLE);
                            fab.setElevation(4);
                        }
                    }, 200);

                    loginLogin = true;
                }
            }
        });
    }

    private void login() {
        String email = edtEmail.getText().toString().trim();
        String password = edtPassword.getText().toString();

        firebaseAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(this, "Login Successfully", Toast.LENGTH_SHORT).show();
                        SharedPreferences.Editor editor=sharedPreferences.edit();
                        editor.putString("email",email);
                        editor.apply();


                        startActivity(new Intent(LoginActivity.this, DashboardActivity.class));
                        // Perform success actions (e.g., navigate to another activity)
                    } else {
                        Toast.makeText(this, "Invalid Credentials", Toast.LENGTH_SHORT).show();

                        // Handle failure (e.g., display an error message)
                    }
                });
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null) {
            selectedImageUri = data.getData();
            imgProfilePicture.setImageURI(selectedImageUri);
            profilePictureUrl = selectedImageUri.toString();
        }
    }

    private boolean validations() {
        if (edtEmail.getText().toString().isEmpty())
        {
            Toast.makeText(this, "Please enter email", Toast.LENGTH_SHORT).show();
            return false;
        }
        else if (edtPassword.getText().toString().isEmpty())
        {
            Toast.makeText(this, "Please enter Password", Toast.LENGTH_SHORT).show();
            return false;
        }
        else {
            return true;
        }
    }
}