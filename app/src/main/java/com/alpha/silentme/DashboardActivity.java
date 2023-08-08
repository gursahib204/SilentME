package com.alpha.silentme;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.alpha.silentme.bean.User;
import com.alpha.silentme.chathead.FloatingViewService;
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
    CardView btnSetLocation;
    CardView btnHandyCalulator;
    ImageView imgProfilePicture;
    SharedPreferences sharedPreferences;
    private static final int CODE_DRAW_OVER_OTHER_APP_PERMISSION = 2084;
    private TextView txtUserName;
    private FirebaseAuth firebaseAuth;
    private DatabaseReference usersReference;
    private TextView txtEmailForm;
    private TextView txtUserNameForm;
    private TextView txtVirtualIDForm;
    private TextView txtCollegeForm;
    private CardView formContainer;
    private ImageButton Virtuaclosebutton;
    private String stUserName="",stEmail="",stVirtualID="",college="";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        initVar();

        setMethods();

        sharedlogout();


        loadUserInfo(); // Load user's name from Firebase
    }

   /* private void loadUserInfo() {
        String userId = firebaseAuth.getCurrentUser().getUid();

        usersReference.child(userId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    User user = dataSnapshot.getValue(User.class);
                    if (user != null) {
                        String userName = user.email;
                        txtUserName.setText(userName); // Set user's name in the TextView
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle error if needed
            }
        });
    }*/
    private void sharedlogout() {
        ImageButton btnLogout = findViewById(R.id.btnLogout);
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

        formContainer.setOnClickListener(view -> {
            Log.e("Bacl","asds");
            // Call the method to load user info from Firebase and display in dialog
            showUserInfoDialog(stUserName,stEmail,stVirtualID,college);
        });

        btnHandyCalulator.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !Settings.canDrawOverlays(DashboardActivity.this)) {


                    //If the draw over permission is not available open the settings screen
                    //to grant the permission.
                    Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                            Uri.parse("package:" + getPackageName()));
                    startActivityForResult(intent, CODE_DRAW_OVER_OTHER_APP_PERMISSION);
                } else {
                    startService(new Intent(DashboardActivity.this, FloatingViewService.class));
                    finish();
                }
            }
        });

        btnSetLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(DashboardActivity.this,MainActivity.class));
            }
        });

        imgProfilePicture.setOnClickListener(view -> {
            // Create a custom dialog for image display
            final Dialog imageDialog = new Dialog(DashboardActivity.this);
            imageDialog.setContentView(R.layout.dialog_image_display);

            // Get the ImageView from the dialog layout
            ImageView dialogImageView = imageDialog.findViewById(R.id.dialogImageView);

            // Load and display the user's profile picture using Glide
            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
            if (user != null) {
                String userId = user.getUid();
                String profilePictureRef = "profile_pictures/" + userId;

                FirebaseStorage storage = FirebaseStorage.getInstance();
                StorageReference storageRef = storage.getReference();
                storageRef.child(profilePictureRef).getDownloadUrl().addOnSuccessListener(uri -> {
                    Glide.with(DashboardActivity.this)
                            .load(uri)
                            .into(dialogImageView);
                }).addOnFailureListener(exception -> {
                    // Handle failure to retrieve the download URL
                    // You can set a placeholder image or handle the error accordingly
                    dialogImageView.setImageResource(R.drawable.image_user);
                });
            }

            // Set up click listener for the close button
            ImageButton btnClose = imageDialog.findViewById(R.id.btnClose);
            btnClose.setOnClickListener(closeView -> imageDialog.dismiss());

            // Show the dialog
            imageDialog.show();
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
        formContainer=findViewById(R.id.formContainer);
        btnSetLocation= findViewById(R.id.btnSetLocation);
        btnHandyCalulator= findViewById(R.id.btnHandyCalculator);
        imgProfilePicture = findViewById(R.id.imgProfilePicture);
        sharedPreferences = getSharedPreferences("session", MODE_PRIVATE);
        String email=sharedPreferences.getString("email","0");
        txtUserName = findViewById(R.id.txtUserName);
        // Initialize FirebaseAuth and DatabaseReference
        firebaseAuth = FirebaseAuth.getInstance();
        usersReference = FirebaseDatabase.getInstance().getReference().child("users");
        txtUserNameForm = findViewById(R.id.userName);
        txtEmailForm = findViewById(R.id.txtEmailForm);
        txtVirtualIDForm = findViewById(R.id.txtVirtualIDForm);
        txtCollegeForm=findViewById(R.id.txtCollegeForm);
        Virtuaclosebutton=findViewById(R.id.vclose);
    }

 private void loadUserInfo() {
        String userId = firebaseAuth.getCurrentUser().getUid();

        usersReference.child(userId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    User user = dataSnapshot.getValue(User.class);
                    if (user != null) {
                        stUserName = user.name;
                        stEmail = user.email;
                        stVirtualID = userId;
                        college=user.college;

                        txtUserName.setText(stUserName); // Set user's name in the TextView
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle error if needed
            }
        });
    }


    private void showUserInfoDialog(String userName, String userEmail, String virtualId, String college) {
        // Create a custom dialog for displaying user information
        final Dialog userInfoDialog = new Dialog(DashboardActivity.this);
        userInfoDialog.setContentView(R.layout.dialog_user_info);

        // Get the TextViews from the dialog layout
        TextView txtUserNameDialog = userInfoDialog.findViewById(R.id.userName);
        TextView txtEmailDialog = userInfoDialog.findViewById(R.id.txtEmailForm);
        TextView txtCollegeDialog = userInfoDialog.findViewById(R.id.txtCollegeForm);
        TextView txtVirtualIDDialog = userInfoDialog.findViewById(R.id.txtVirtualIDForm);


        // Set user information in the TextViews
        txtUserNameDialog.setText(userName);
        txtEmailDialog.setText(userEmail);
        txtCollegeDialog.setText(college);
        txtVirtualIDDialog.setText("Virtual ID: " + virtualId);

        Virtuaclosebutton = userInfoDialog.findViewById(R.id.vclose);
        Virtuaclosebutton.setOnClickListener(closeView -> userInfoDialog.dismiss());


        // Show the dialog
        userInfoDialog.show();
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CODE_DRAW_OVER_OTHER_APP_PERMISSION) {
            //Check if the permission is granted or not.
            if (resultCode == RESULT_OK)
            {
                startService(new Intent(this, FloatingViewService.class));
                //finish();
            } else { //Permission is not available
                Toast.makeText(this,
                        "Draw over other app permission not available. Closing the application",
                        Toast.LENGTH_SHORT).show();

                //finish();
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

}



