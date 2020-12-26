package net.glochat.dev.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import net.glochat.dev.R;
import net.glochat.dev.view.SelectedPicEditView;
import net.glochat.dev.viewholder.SelectedPicEditViewHolder;

import java.util.List;

public class SelectedPicEditAdapter extends RecyclerView.Adapter<SelectedPicEditViewHolder> {

    private List<String> list;
    private Context context;
    private SelectedPicEditView view;

    public SelectedPicEditAdapter(Context context, List<String> list, SelectedPicEditView view){
        this.list = list;
        this.context = context;
        this.view = view;
    }

    @NonNull
    @Override
    public SelectedPicEditViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.single_pic_view_layout2, parent, false);
        return new SelectedPicEditViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SelectedPicEditViewHolder holder, int position) {
        holder.initView(context, list.get(position), view);
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

}
