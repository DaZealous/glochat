package net.glochat.dev.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import net.glochat.dev.R;
import net.glochat.dev.models.Users;
import net.glochat.dev.view.VoiceVideoCallView;
import net.glochat.dev.viewholder.UsersCallViewHolder;

import java.util.List;

public class UsersCallAdapter extends RecyclerView.Adapter<UsersCallViewHolder> {

    private List<Users> list;
    private Context context;
    private VoiceVideoCallView view;

    public UsersCallAdapter(Context context, List<Users> list, VoiceVideoCallView view){
        this.list = list;
        this.context = context;
        this.view = view;
    }

    @NonNull
    @Override
    public UsersCallViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.users_video_audio_layout, parent, false);
        return new UsersCallViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull UsersCallViewHolder holder, int position) {
        holder.initView(context, list.get(position), view);
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

}
