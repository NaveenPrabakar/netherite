package com.example.androidexample.Editor;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.androidexample.R;

import java.util.ArrayList;
import java.util.List;

public class MessagePopulator extends RecyclerView.Adapter<MessagePopulator.MessageViewHolder> {
    private List<Message> messages;
    private Context context;

    public MessagePopulator(List<Message> messages, Context context) {
        this.messages = messages;
        this.context = context;
        setHasStableIds(true);
    }

    public void addMessage(Message message)
    {
        if (messages == null)
        {
            messages = new ArrayList<>();
        }
        messages.add(message);
        //notifyItemInserted(messages.size());
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public MessageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.message_item, parent, false);
        return new MessageViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MessageViewHolder holder, int position) {
        String content = messages.get(position).getContent();
        String user = messages.get(position).getUsername();
        //String content = messages.get(position).getString("content");
        holder.messageTextView.setText(content);
        holder.usernameTextView.setText(user);
    }

    @Override
    public int getItemCount() {
        return messages.size();
    }

    @Override
    public long getItemId(int position) {
        // Use a unique ID for each message. Here, we’re assuming each message has a unique `content` and `username` combination.
        Message message = messages.get(position);
        return (message.getContent() + message.getUsername()).hashCode();
    }

    public static class MessageViewHolder extends RecyclerView.ViewHolder {
        TextView messageTextView;
        TextView usernameTextView;

        public MessageViewHolder(View itemView) {
            super(itemView);
            messageTextView = itemView.findViewById(R.id.message_text);
            usernameTextView = itemView.findViewById(R.id.sender_name);
        }
    }
}

