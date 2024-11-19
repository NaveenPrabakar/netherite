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

/**
 * Used to populate RecyclerViews with {@link Message} objects.
 * This class requires a list of Message Objects to be passed in and creates a ViewHolder for each Message.
 * @author Jamey Nguyen
 */
public class MessagePopulator extends RecyclerView.Adapter<MessagePopulator.MessageViewHolder> {
    /**
     * List of messages to be displayed.
     */
    private List<Message> messages;
    /**
     * Context of the application.
     */
    private Context context;

    /**
     * Constructor for the MessagePopulator.
     * @param messages List of messages to be displayed.
     * @param context Context of the application.
     */
    public MessagePopulator(List<Message> messages, Context context) {
        this.messages = messages;
        this.context = context;
        setHasStableIds(true);
    }

    /**
     * Adds a message to the list of messages to be displayed.
     * @param message Message to be added.
     */
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

    /**
     * Called when RecyclerView needs a new {@link MessageViewHolder} of the given type to represent.
     * For the specific case use of ChatActivity, this method inflates the layout for {@code message_item.xml}.
     * @param parent   The ViewGroup into which the new View will be added after it is bound to
     *                 an adapter position.
     * @param viewType The view type of the new View.
     * @return A new ViewHolder that holds a View of the given view type.
     */
    @NonNull
    @Override
    public MessageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.message_item, parent, false);
        return new MessageViewHolder(view);
    }

    /**
     * Called by RecyclerView to display the data at the specified position.
     * @param holder   The ViewHolder which should be updated to represent the contents of the
     *                 item at the given position in the data set.
     * @param position The position of the item within the adapter's data set.
     */
    @Override
    public void onBindViewHolder(@NonNull MessageViewHolder holder, int position) {
        String content = messages.get(position).getContent();
        String user = messages.get(position).getUsername();
        //String content = messages.get(position).getString("content");
        holder.messageTextView.setText(content);
        holder.usernameTextView.setText(user);
    }

    /**
     * Returns the total number of items in the data set held by the adapter.
     * @return The total number of items in this adapter.
     */
    @Override
    public int getItemCount() {
        return messages.size();
    }

    /**
     * Returns the stable ID for the item at the given position.
     * @param position Adapter position to query
     * @return The stable ID of the item at position
     */
    @Override
    public long getItemId(int position) {
        // Use a unique ID for each message. Here, we’re assuming each message has a unique `content` and `username` combination.
        Message message = messages.get(position);
        return (message.getContent() + message.getUsername()).hashCode();
    }

    /**
     * ViewHolder for a message.
     */
    public static class MessageViewHolder extends RecyclerView.ViewHolder {
        /**
         * Text view for the message content.
         */
        TextView messageTextView;
        /**
         * Text view for the sender's username.
         */
        TextView usernameTextView;

        /**
         * Constructor for a MessageViewHolder.
         * @param itemView The view for this ViewHolder.
         */
        public MessageViewHolder(View itemView) {
            super(itemView);
            messageTextView = itemView.findViewById(R.id.message_text);
            usernameTextView = itemView.findViewById(R.id.sender_name);
        }
    }
}

