package com.alpha.silentme.firebase_chat;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import com.alpha.silentme.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class ChatActivity extends AppCompatActivity {
    RecyclerView recyclerView;
    ChatRecyclerAdapter chatAdapter;
    private EditText edtMsg;
    DatabaseReference chatRef = null;
    private ArrayList<BeanChatRecyclerViewApp> messagesList;


    String chatID,otherName,myEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        chatID = getIntent().getStringExtra("chatID");
        otherName = getIntent().getStringExtra("otherName");
        myEmail = getIntent().getStringExtra("currentEmail");

        initVars();
        fetchMessages();
    }

    private void initVars() {
        edtMsg = (EditText) findViewById(R.id.chat_window_edt_msg);

        messagesList = new ArrayList<>();

        //Recycler Adapter
        chatAdapter = new ChatRecyclerAdapter(messagesList,myEmail);

        recyclerView = (RecyclerView)findViewById(R.id.chat_app_list_panel);

        RecyclerView.LayoutManager layout = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layout);
        recyclerView.setAdapter(chatAdapter);


    }

    private void fetchMessages() {

        Log.e("chatID>>",chatID);
        chatID = "98923473894892483208";
        Log.e("chatID>>",chatID);

        chatRef = FirebaseDatabase.getInstance().getReference().child("chat").child(chatID);

        chatRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (!dataSnapshot.exists()) {
                    // Chat room doesn't exist, so create it
                    chatRef.setValue(true); // You can set any value, like true or a timestamp
                }

                chatRef.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        messagesList.clear();
                        for (DataSnapshot eachmsg: snapshot.getChildren())
                        {
                            BeanChatRecyclerViewApp value = eachmsg.getValue(BeanChatRecyclerViewApp.class);
                            messagesList.add(value);

                        }
                        chatAdapter.notifyDataSetChanged();
                        recyclerView.scrollToPosition(messagesList.size()-1);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
                // Now fetch and display messages using the chatRef
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e("error", "Failed to check chat existence: " + databaseError.getMessage());
            }
        });

    }

    public void sendMessage(View view) {
        Log.i("info","clicked");
        if (edtMsg.getText().toString().length()>0){
            Log.i("info","clicked");
            final String msg = edtMsg.getText().toString();
            //This is our message which we want ot save
            //Update to list
//            messagesList.add(new BeanChatRecyclerViewApp(msg,myEmail,new SimpleDateFormat("HH:mm a").format(new Date())));
//            chatAdapter.notifyDataSetChanged();

            //Creating bean message

            BeanChatRecyclerViewApp message = new BeanChatRecyclerViewApp(msg,myEmail,new SimpleDateFormat("HH:mm a").format(new Date()));
            chatRef.push().setValue(message)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()){
                                Log.i("info","sucess");
                            }
                        }
                    });
        }
        recyclerView.scrollToPosition(messagesList.size()-1);

        edtMsg.setText("");
    }
}