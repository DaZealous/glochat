package net.glochat.dev.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import net.glochat.dev.R;
import net.glochat.dev.models.Users;
import net.glochat.dev.viewholder.UsersListViewHolder;

import java.util.List;

public class UsersListAdapter extends RecyclerView.Adapter<UsersListViewHolder> {

    private List<Users> list;
    private Context context;

    public UsersListAdapter(Context context, List<Users> list){
        this.list = list;
        this.context = context;
    }

    @NonNull
    @Override
    public UsersListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.users_list_layout, parent, false);
        return new UsersListViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull UsersListViewHolder holder, int position) {
        holder.initView(context, list.get(position));
    }

    @Override
    public int getItemCount() {
        return list.size();
    }
}
