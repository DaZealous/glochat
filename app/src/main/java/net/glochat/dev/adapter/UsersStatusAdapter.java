package net.glochat.dev.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import net.glochat.dev.R;
import net.glochat.dev.models.Users;
import net.glochat.dev.viewholder.UsersStatusViewHolder;

import java.util.ArrayList;

public class UsersStatusAdapter extends RecyclerView.Adapter<UsersStatusViewHolder> {

    private ArrayList<Users> list;
    private Context context;

    public UsersStatusAdapter(Context context, ArrayList<Users> list){
        this.list = list;
        this.context = context;
    }

    @NonNull
    @Override
    public UsersStatusViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.single_user_status_layout, parent, false);
        return new UsersStatusViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull UsersStatusViewHolder holder, int position) {
        holder.initView(context, list.get(position), position);
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

}
