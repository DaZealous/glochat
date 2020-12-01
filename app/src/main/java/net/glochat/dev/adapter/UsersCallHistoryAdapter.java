package net.glochat.dev.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import net.glochat.dev.R;
import net.glochat.dev.models.Users;
import net.glochat.dev.viewholder.UsersCallHistoryViewHolder;

import java.util.List;

public class UsersCallHistoryAdapter extends RecyclerView.Adapter<UsersCallHistoryViewHolder> {

    private List<Users> list;
    private Context context;

    public UsersCallHistoryAdapter(Context context, List<Users> list){
        this.list = list;
        this.context = context;
    }

    @NonNull
    @Override
    public UsersCallHistoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.users_video_history_layout, parent, false);
        return new UsersCallHistoryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull UsersCallHistoryViewHolder holder, int position) {
        holder.initView(context, list.get(position));
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

}
