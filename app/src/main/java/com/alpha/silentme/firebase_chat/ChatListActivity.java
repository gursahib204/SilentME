package com.alpha.silentme.firebase_chat;

import static com.alpha.silentme.consts.Constants.CONST_COLLEGE_NAME;
import static com.alpha.silentme.consts.Constants.CONST_CURRENT_EMAIL;
import static com.alpha.silentme.consts.Constants.MYUUID;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;

import com.alpha.silentme.MySharedPreferences;
import com.alpha.silentme.R;
import com.alpha.silentme.bean.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Objects;

public class ChatListActivity extends AppCompatActivity {

    private ChatListAdapter adptr;
    private ArrayList<User> arrayList = new ArrayList<>();
    RecyclerView chat_app_list;
    String collegeName = "";
    String currentEmail = "";
    private String myUUID= "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_list);

        //Initilize variables
        initilize();

    }

    private void initilize() {

        chat_app_list = findViewById(R.id.chat_app_list);
        fetchUsers();
    }

    private void fetchUsers() {

        FirebaseDatabase instance = FirebaseDatabase.getInstance();
        DatabaseReference usersRef = instance.getReference("users");


        collegeName = MySharedPreferences.getString(ChatListActivity.this, CONST_COLLEGE_NAME);
        currentEmail = MySharedPreferences.getString(ChatListActivity.this, CONST_CURRENT_EMAIL);
        myUUID = MySharedPreferences.getString(ChatListActivity.this, MYUUID);
        usersRef.orderByChild("college").equalTo(collegeName).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot eachTeacher : snapshot.getChildren()) {
                    Log.e("eachUser", Objects.requireNonNull(eachTeacher.getValue()).toString());
                    String key = eachTeacher.getKey();
                    User eachUser = eachTeacher.getValue(User.class);
                    if (!eachUser.getEmail().equals(currentEmail))
                        arrayList.add(eachUser);
                }
                updateAdatpter();
                Log.e("users", arrayList.toString());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void updateAdatpter() {
        adptr = new ChatListAdapter(arrayList,myUUID,currentEmail);

        RecyclerView.LayoutManager layout = new LinearLayoutManager(this);
        chat_app_list.setLayoutManager(layout);
        chat_app_list.setAdapter(adptr);

    }
}