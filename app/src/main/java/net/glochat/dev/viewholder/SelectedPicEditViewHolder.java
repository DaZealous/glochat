package net.glochat.dev.viewholder;

import android.content.Context;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import net.glochat.dev.R;
import net.glochat.dev.view.SelectedPicEditView;

public class SelectedPicEditViewHolder extends RecyclerView.ViewHolder {

    private ImageView imageView;
    private ImageButton imgRemove;

    public SelectedPicEditViewHolder(@NonNull View itemView) {
        super(itemView);
        imageView = itemView.findViewById(R.id.single_pic_view_layout_img_view);
        imgRemove = itemView.findViewById(R.id.single_pic_view_layout_selected_img);
    }

    public void initView(Context context, String image, SelectedPicEditView view) {

        Glide.with(context.getApplicationContext()).load(image).placeholder(R.drawable.profile_placeholder).into(imageView);

        imageView.setOnClickListener(view1 -> view.selectImage(image, getAdapterPosition()));
        imgRemove.setOnClickListener(view1 -> view.removeImage(image, getAdapterPosition()));

    }

}
