package com.example.androidexample.FileView;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.example.androidexample.Editor.TextActivity;
import com.example.androidexample.R;
import com.example.androidexample.UserPreferences;
import com.example.androidexample.Volleys.VolleySingleton;
import com.example.androidexample.WebSockets.WebSocketManager;

import java.util.List;

/*
 * Custom class to populate the RecyclerView with the list of photo names.
 * Give a list of photos and pass it into the constructor, (along with the context) and it will spit out the list
 * on the recycler display.
 */
public class RecentFilePopulator extends RecyclerView.Adapter<RecentFilePopulator.ViewHolder> {
    private List<String> fileNames;
    private Context context;
    private String email;
    private String password;
    private String username;
    private String aiURL;

    /*
     * @param photoNames list of photo file names to display, retrieved from the server.
     * @param context context of the started activity.
     */
    public RecentFilePopulator(List<String> fileNames, Context context, String email, String password, String username)
    {
        this.fileNames = fileNames;
        this.context = context;
        this.email = email;
        this.password = password;
        this.username = username;
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
                .inflate(R.layout.recent_file_item, parent, false);
        return new ViewHolder(view);
    }

    /*
     * Adds the functionality to the actual ViewHolder.
     */
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.textViewFileName.setText(fileNames.get(position));
        // Click the photo file name to actually view it.
        holder.itemView.setOnClickListener(v -> {
//            Intent i = new Intent(context, TextActivity.class);
////            i.putExtra("FILENAME", photoNames.get(position));
////            i.putExtra("EMAIL", email);
////            i.putExtra("USERNAME", username);
////            i.putExtra("PASSWORD", password);
//            context.startActivity(i);
            getFile(fileNames.get(position));
        });
    }

    @Override
    public int getItemCount() {
        return fileNames.size();
    }

    /*
     * Required to use RecyclerView.
     * This class is the class that actually holds the views for each photo item.
     */
    public static class ViewHolder extends RecyclerView.ViewHolder {
        // The actual textview displaying the photo file name. i.e Test.txt
        TextView textViewFileName;

        public ViewHolder(View itemView) {
            super(itemView);
            textViewFileName = itemView.findViewById(R.id.textViewFileName);
        }
    }

    /**
     * Retrieves the file's unique ID from the server and connects to its WebSocket instance.
     * Loads the file's content for editing in a `TextActivity`.
     *
     * @param fileName the name of the file to retrieve
     */
    private void getFile(String fileName){
        Uri.Builder builder = Uri.parse(UserPreferences.getFileID()).buildUpon();
        builder.appendQueryParameter("email", email);
        builder.appendQueryParameter("fileName", fileName );
        String url = builder.build().toString();

        StringRequest stringRequest = new StringRequest(
                Request.Method.GET,
                url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d("Volley Response", response);
                        int id = Integer.parseInt(response);

                        String serverUrl = UserPreferences.getUrlWs() + id;
                        aiURL = UserPreferences.getUrlAIws() + id + "/" + username;

                        Log.d("Instance URL", aiURL);
                        WebSocketManager.getInstance().connectWebSocket(serverUrl);
                        getFileString(fileName);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // Handle any errors that occur during the request
                        Log.e("Volley Error", error.toString());
                    }
                }
        );

        // Adding request to request queue
        VolleySingleton.getInstance(context).addToRequestQueue(stringRequest);
    }

    /**
     * Retrieves the file's content as a string from the server and starts the `TextActivity`
     * to display and edit the file content.
     *
     * @param fileName the name of the file to retrieve
     */
    private void getFileString(String fileName){
        Uri.Builder builder = Uri.parse(UserPreferences.getUrlFilesPull()).buildUpon();
        builder.appendQueryParameter("email", email);
        builder.appendQueryParameter("password", password);
        builder.appendQueryParameter("fileName", fileName );
        String url = builder.build().toString();

        StringRequest stringRequest = new StringRequest(
                Request.Method.GET,
                url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d("Volley Response", response);

                        Intent intent = new Intent(context, TextActivity.class);
                        intent.putExtra("FILEKEY", fileName);
                        intent.putExtra("CONTENT", response);
                        intent.putExtra("AIWSURL", aiURL);
                        context.startActivity(intent);

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // Handle any errors that occur during the request
                        Log.e("Volley Error", error.toString());
                    }
                }
        );

        // Adding request to request queue
        VolleySingleton.getInstance(context).addToRequestQueue(stringRequest);
    }
}
