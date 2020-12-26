package net.glochat.dev.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import net.glochat.dev.R;
import net.glochat.dev.view.ChoosePicView;
import net.glochat.dev.viewholder.ChoosePicViewHolder;

import java.util.List;

public class ChoosePicAdapter extends RecyclerView.Adapter<ChoosePicViewHolder> {

    private List<String> list;
    private Context context;
    private ChoosePicView view;

    public ChoosePicAdapter(Context context, List<String> list, ChoosePicView view){
        this.list = list;
        this.context = context;
        this.view = view;
    }

    @NonNull
    @Override
    public ChoosePicViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.single_pic_view_layout, parent, false);
        return new ChoosePicViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ChoosePicViewHolder holder, int position) {
        holder.initView(context, list.get(position), view);
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

}
