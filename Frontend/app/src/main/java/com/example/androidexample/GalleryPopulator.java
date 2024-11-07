package com.example.androidexample;

// PhotoNameAdapter.java
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class GalleryPopulator extends RecyclerView.Adapter<GalleryPopulator.ViewHolder> {
    private List<String> photoNames;

    public GalleryPopulator(List<String> photoNames)
    {
        this.photoNames = photoNames;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.photo_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.textViewPhotoName.setText(photoNames.get(position));
    }

    @Override
    public int getItemCount() {
        return photoNames.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView textViewPhotoName;

        public ViewHolder(View itemView) {
            super(itemView);
            textViewPhotoName = itemView.findViewById(R.id.textViewPhotoName);
        }
    }
}

