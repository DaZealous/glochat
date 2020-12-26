package net.glochat.dev.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import net.glochat.dev.R;

import java.util.ArrayList;

public class NewsFeedImageAdapter extends RecyclerView.Adapter<NewsFeedImageAdapter.MyViewHolder> {
    private ArrayList<String> images;
    private Context context;

    public NewsFeedImageAdapter(ArrayList<String> images, Context context){
        this.images = images;
        this.context = context;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new NewsFeedImageAdapter.MyViewHolder(LayoutInflater.from(context).inflate(R.layout.single_news_feed_image_view_layout, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        Glide.with(context)
                .load(images.get(position))
                .centerCrop()
                .into(holder.imageView);
    }

    @Override
    public int getItemCount() {
        return images.size();
    }

    static class MyViewHolder extends RecyclerView.ViewHolder{

        ImageView imageView;

        public MyViewHolder(@NonNull View view) {
            super(view);
            imageView = view.findViewById(R.id.single_news_feed_image_view);
        }
    }
}
