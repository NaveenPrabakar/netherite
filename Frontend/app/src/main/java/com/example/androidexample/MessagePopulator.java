package com.example.androidexample;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class MessagePopulator extends RecyclerView.Adapter<MessagePopulator.MessageViewHolder> {
    private List<Message> messages;
    private Context context;

    public MessagePopulator(List<Message> messages, Context context) {
        this.messages = messages;
        this.context = context;
    }

//    public MessagePopulator(Message message, Context context)
//    {
//        if (messages == null)
//        {
//            messages = new ArrayList<>();
//        }
//        messages.add(message);
//        this.context = context;
//    }

    @NonNull
    @Override
    public MessageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.message_item, parent, false);
        return new MessageViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MessageViewHolder holder, int position) {
        String content = messages.get(position).getContent();
        //String content = messages.get(position).getString("content");
        holder.messageTextView.setText(content);
    }

    @Override
    public int getItemCount() {
        return messages.size();
    }

    public static class MessageViewHolder extends RecyclerView.ViewHolder {
        TextView messageTextView;

        public MessageViewHolder(View itemView) {
            super(itemView);
            messageTextView = itemView.findViewById(R.id.message_text);
        }
    }
}

