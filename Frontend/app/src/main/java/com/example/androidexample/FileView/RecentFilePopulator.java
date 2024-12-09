package com.example.androidexample.FileView;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.androidexample.Editor.TextActivity;
import com.example.androidexample.R;

import java.util.List;

/*
 * Custom class to populate the RecyclerView with the list of photo names.
 * Give a list of photos and pass it into the constructor, (along with the context) and it will spit out the list
 * on the recycler display.
 */
public class RecentFilePopulator extends RecyclerView.Adapter<RecentFilePopulator.ViewHolder> {
    private List<String> photoNames;
    private Context context;
    private String username;
    private String password;
    private String email;

    /*
     * @param photoNames list of photo file names to display, retrieved from the server.
     * @param context context of the started activity.
     */
    public RecentFilePopulator(List<String> photoNames, Context context, String username, String email, String password)
    {
        this.photoNames = photoNames;
        this.context = context;
        this.username = username;
        this.email = email;
        this.password = password;
    }

    /*
     * Called when the RecyclerView needs a new ViewHolder. Think of it as creating a new <div>.
     * A ViewGroup can hold multiple types of views, that is, LinearLayout, TextView, ScrollView, for common examples.
     * A ViewHolder is a wrapper around a View that contains the layout for an individual item in the list. Incredibly efficient.
     *
     * @param parent The viewgroup that the new view will be added to. A viewgroup is a special type of 'View'.
     * @param viewType The view type of the new view
     * @return A new ViewHolder that holds the view for each photo item. A ViewHolder stores references of a view.
     */
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.photo_item, parent, false);
        return new ViewHolder(view);
    }

    /*
     * Adds the functionality to the actual ViewHolder.
     */
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.textViewPhotoName.setText(photoNames.get(position));
        // Click the photo file name to actually view it.
        holder.itemView.setOnClickListener(v -> {
            Intent i = new Intent(context, TextActivity.class);
//            i.putExtra("FILENAME", photoNames.get(position));
//            i.putExtra("EMAIL", email);
//            i.putExtra("USERNAME", username);
//            i.putExtra("PASSWORD", password);
            context.startActivity(i);
        });
    }

    @Override
    public int getItemCount() {
        return photoNames.size();
    }

    /*
     * Required to use RecyclerView.
     * This class is the class that actually holds the views for each photo item.
     */
    public static class ViewHolder extends RecyclerView.ViewHolder {
        // The actual textview displaying the photo file name. i.e Test.png
        TextView textViewPhotoName;

        public ViewHolder(View itemView) {
            super(itemView);
            textViewPhotoName = itemView.findViewById(R.id.textViewPhotoName);
        }
    }
}
