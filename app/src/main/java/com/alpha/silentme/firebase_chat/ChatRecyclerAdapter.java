package com.alpha.silentme.firebase_chat;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;


import com.alpha.silentme.R;

import java.util.ArrayList;

public class ChatRecyclerAdapter extends RecyclerView.Adapter<ChatRecyclerAdapter.RecyclerViewHolder> {

    static Context context;
    String myEmail;
    ArrayList<BeanChatRecyclerViewApp> array;

    public ChatRecyclerAdapter(ArrayList<BeanChatRecyclerViewApp> inpData, String myEmail)
    {
        array=inpData;
        this.myEmail=myEmail;

    }

    @Override
    public RecyclerViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        context=parent.getContext();
        LayoutInflater inf= LayoutInflater.from(parent.getContext());
        View view=(View)inf.inflate(R.layout.chat_app_single_message_design,parent,false);
        RecyclerViewHolder holder=new RecyclerViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(RecyclerViewHolder holder, int position)
    {
        BeanChatRecyclerViewApp msg=array.get(position);
        if (msg.getEmail().equals(myEmail)){
            //update our
            holder.layoutSender.setVisibility(View.VISIBLE);
            holder.layoutReciver.setVisibility(View.INVISIBLE);
            //sender side Hide
            holder.senderMsg.setVisibility(View.INVISIBLE);
            holder.senderTime.setVisibility(View.INVISIBLE);

            //Update our msg
            holder.meMsg.setVisibility(View.VISIBLE);
            holder.meMsg.setText(msg.getMsg());
            holder.myTime.setVisibility(View.VISIBLE);
            holder.myTime.setText(msg.getTime());
        }else{
            //Update sender
            holder.layoutSender.setVisibility(View.INVISIBLE);
            holder.layoutReciver.setVisibility(View.VISIBLE);
            //Hide sender
            holder.meMsg.setVisibility(View.INVISIBLE);
            holder.myTime.setVisibility(View.INVISIBLE);

            //Update sender msg
            holder.senderMsg.setVisibility(View.VISIBLE);
            holder.senderMsg.setText(msg.getMsg());
            holder.senderTime.setVisibility(View.VISIBLE);
            holder.senderTime.setText(msg.getTime());
        }

    }

    @Override
    public int getItemCount() {
        return array.size();
    }

    public static class RecyclerViewHolder extends RecyclerView.ViewHolder
    {
        TextView meMsg,senderMsg,senderTime,myTime;
        LinearLayout layoutSender,layoutReciver;

        RecyclerViewHolder(View view)
        {
            super(view);
            meMsg = (TextView)view.findViewById(R.id.chat_single_list_my_msg);
            senderMsg = (TextView)view.findViewById(R.id.chat_single_list_sender_msg);
            myTime = (TextView)view.findViewById(R.id.chat_single_list_my_time);
            senderTime = (TextView)view.findViewById(R.id.chat_single_list_sender_time);

            layoutSender = (LinearLayout)view.findViewById(R.id.layout_sender);
            layoutReciver = (LinearLayout)view.findViewById(R.id.layout_me);
        }
    }

}
