package net.glochat.dev.viewholder;

import android.content.Context;
import android.net.Uri;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import net.glochat.dev.R;
import net.glochat.dev.view.ChoosePicView;

import java.io.File;

public class ChoosePicViewHolder extends RecyclerView.ViewHolder {

    private ImageView imageView;
    private ImageButton imgChecked;

    public ChoosePicViewHolder(@NonNull View itemView) {
        super(itemView);
        imageView = itemView.findViewById(R.id.single_pic_view_layout_img_view);
        imgChecked = itemView.findViewById(R.id.single_pic_view_layout_selected_img);
    }

    public void initView(Context context, String image, ChoosePicView view) {

        Glide.with(context.getApplicationContext()).load(image).placeholder(R.drawable.profile_placeholder).into(imageView);

        if (view.imageExist(getUriString(image)))
            setVisibility(View.VISIBLE);
        else
            setVisibility(View.GONE);

        imageView.setOnClickListener(view1 -> {
            if (view.imageExist(getUriString(image))) {
                view.removeImage(getUriString(image));
                setVisibility(View.GONE);
            } else {
                view.addImage(getUriString(image));
                setVisibility(View.VISIBLE);
            }
        });

    }

    private String getUriString(String image) {
        return Uri.fromFile(new File(image)).toString();
    }

    private void setVisibility(int visibility) {
        imgChecked.setVisibility(visibility);
    }

}
