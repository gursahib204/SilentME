package com.alpha.silentme;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.alpha.silentme.bean.User;
import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class DashboardActivity extends AppCompatActivity {


    //Test
    Button btnSetLocation;
    Button btnHandyCalulator;
    ImageView imgProfilePicture;
    SharedPreferences sharedPreferences;
    private TextView txtUserName;
    private FirebaseAuth firebaseAuth;
    private DatabaseReference usersReference;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        initVar();

        setMethods();

        sharedlogout();
        loadUserInfo(); // Load user's name from Firebase
    }

    private void loadUserInfo() {
        String userId = firebaseAuth.getCurrentUser().getUid();

        usersReference.child(userId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    User user = dataSnapshot.getValue(User.class);
                    if (user != null) {
                        String userName = user.name;
                        txtUserName.setText(userName); // Set user's name in the TextView
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle error if needed
            }
        });
    }
    private void sharedlogout() {
        Button btnLogout = findViewById(R.id.btnLogout);
        btnLogout.setOnClickListener(view -> {
            // Clear the SharedPreferences and navigate to LoginActivity
            SharedPreferences.Editor editor=sharedPreferences.edit();
            editor.remove("email");
            editor.commit();
            Intent i = new Intent(getApplicationContext(), LoginActivity.class);
            startActivity(i);
            finish();
        });
    }

    private void setMethods() {
        btnSetLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(DashboardActivity.this,MainActivity.class));
            }
        });

        loadAndDisplayProfilePicture();
    }

    private void loadAndDisplayProfilePicture() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            String userId = user.getUid();

            // Construct the reference to the user's profile picture in Firebase Storage
            // Assuming you are using "profile_pictures" as the storage path
            String profilePictureRef = "profile_pictures/" + userId;

            // Get the download URL for the profile picture
            FirebaseStorage storage = FirebaseStorage.getInstance();
            StorageReference storageRef = storage.getReference();
            storageRef.child(profilePictureRef).getDownloadUrl().addOnSuccessListener(uri -> {
                // Load the image using Glide and display it in the ImageView
                Glide.with(this)
                        .load(uri)
                        .into(imgProfilePicture);
            }).addOnFailureListener(exception -> {
                // Handle failure to retrieve the download URL
                // You can set a placeholder image or handle the error accordingly
                imgProfilePicture.setImageResource(R.drawable.ic_launcher_background);
            });
        }
    }



    private void initVar() {
        btnSetLocation= findViewById(R.id.btnSetLocation);
        btnHandyCalulator= findViewById(R.id.btnHandyCalculator);
        imgProfilePicture = findViewById(R.id.imgProfilePicture);
        sharedPreferences = getSharedPreferences("session", MODE_PRIVATE);
        String email=sharedPreferences.getString("email","0");
        txtUserName = findViewById(R.id.txtUserName);
        // Initialize FirebaseAuth and DatabaseReference
        firebaseAuth = FirebaseAuth.getInstance();
        usersReference = FirebaseDatabase.getInstance().getReference().child("users");
    }
}