package com.alpha.silentme.firebase_chat;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.alpha.silentme.DashboardActivity;
import com.alpha.silentme.R;
import com.alpha.silentme.bean.User;
import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


public class ChatListAdapter extends RecyclerView.Adapter<ChatListAdapter.RecyclerViewHolder>
{

    static Context context;
    ArrayList<User> array;
    String myUUID,myEmail;
    ChatListAdapter(ArrayList<User> arrayy, String myUUID, String myEmail)
    {
        this.myUUID = myUUID;
        this.myEmail = myEmail;
        array=arrayy;
    }
    @Override
    public RecyclerViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        context=parent.getContext();
        LayoutInflater inf= LayoutInflater.from(parent.getContext());
        View view=(View)inf.inflate(R.layout.chat_list_single_item_design,parent,false);
        RecyclerViewHolder holder=new RecyclerViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerViewHolder holder, int position) {
        User chatListBean = array.get(position);
        String uId = String.valueOf(chatListBean.getId());
        holder.name.setText(chatListBean.getName());
        holder.email.setText(chatListBean.getEmail());

        Glide.with(context)
                .load(chatListBean.getProfilePictureUrl())
                .into(holder.img);

        holder.relativeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String chatID = getChatID(uId);
                Intent intent = new Intent(context, ChatActivity.class);
                intent.putExtra("chatID",chatID);
                intent.putExtra("currentEmail",myEmail);
                intent.putExtra("otherName",chatListBean.getName());
                context.startActivity(intent);
            }
        });
    }

    private String getChatID(String otherUID) {
        return sortEmails(myUUID, otherUID);
    }

    private String combineEmails(List<String> emails) {
        StringBuilder combinedEmails = new StringBuilder();
        for (String email : emails) {
            combinedEmails.append(email).append("\n"); // Use a separator if needed
        }
        return combinedEmails.toString();
    }
    private String sortEmails(String myEmail, String otherEmail) {
        List<String> emails = new ArrayList<>();
        emails.add(myEmail);
        emails.add(otherEmail);

        Collections.sort(emails);
        String chatID = combineEmails(emails);
        return chatID;

    }

    @Override
    public int getItemCount() {
        return array.size();
    }

    public static class RecyclerViewHolder extends RecyclerView.ViewHolder
    {
        ImageView img;
        TextView name,email,unread;
        RelativeLayout relativeLayout;
        RecyclerViewHolder(View view)
        {
            super(view);
            img=(ImageView)view.findViewById(R.id.chat_list_image_person);
            name=(TextView)view.findViewById(R.id.chat_list_name);
            email=(TextView)view.findViewById(R.id.chat_list_email);
            relativeLayout=(RelativeLayout) view.findViewById(R.id.chat_list_relative);
            unread = (TextView) view.findViewById(R.id.chat_list_no_of_unread_msg);
        }
    }
}

