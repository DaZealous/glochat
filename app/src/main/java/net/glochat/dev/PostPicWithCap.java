package net.glochat.dev;

import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import net.glochat.dev.adapter.SelectedPicEditAdapter;
import net.glochat.dev.models.PicPostModel;
import net.glochat.dev.utils.SharedPref;
import net.glochat.dev.view.SelectedPicEditView;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;

public class PostPicWithCap extends AppCompatActivity implements SelectedPicEditView {

    private EditText editCaption;
    private TextView btnPostText;
    private ProgressBar btnPostProgress;
    private ArrayList<String> selectedImages = null;
    private SelectedPicEditAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_pic_with_cap);

        editCaption = findViewById(R.id.activity_pic_cap_post_edit_text_post);
        RelativeLayout btnPost = findViewById(R.id.activity_pic_cap_post_btn_post);
        btnPostText = findViewById(R.id.activity_pic_cap_post_btn_post_proceed_text);
        btnPostProgress = findViewById(R.id.activity_pic_cap_post_btn_post_progress_bar);
        RecyclerView recyclerView = findViewById(R.id.activity_pic_cap_post_recycler);

        if (getIntent().getStringArrayListExtra("selectedImages") != null)
            selectedImages = getIntent().getStringArrayListExtra("selectedImages");

        adapter = new SelectedPicEditAdapter(this, selectedImages, this);

        LinearLayoutManager manager = new LinearLayoutManager(this);
        manager.setOrientation(LinearLayoutManager.HORIZONTAL);
        recyclerView.setLayoutManager(manager);
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(adapter);
        adapter.notifyDataSetChanged();

        btnPost.setOnClickListener(view -> {
            if (TextUtils.isEmpty(editCaption.getText().toString())) {
                Toast.makeText(this, "Type a caption", Toast.LENGTH_SHORT).show();
                return;
            }

            btnPostProgress.setVisibility(View.VISIBLE);
            btnPostText.setVisibility(View.GONE);
            new Handler().postDelayed(() -> {
                btnPostProgress.setVisibility(View.GONE);
                btnPostText.setVisibility(View.VISIBLE);
                EventBus.getDefault().post(new PicPostModel(new SharedPref(PostPicWithCap.this).getUserId(), selectedImages, editCaption.getText().toString(), new SharedPref(PostPicWithCap.this).getUsername(), new SharedPref(PostPicWithCap.this).getImage_url(), "1", System.currentTimeMillis(), "false", "2400", "278"));
                finish();
            }, 3000);
        });

    }

    @Override
    public void selectImage(String image, int newPos) {

    }

    @Override
    public int getPrevPos() {
        return 0;
    }

    @Override
    public void removeImage(String image, int pos) {
        new AlertDialog.Builder(this)
                .setTitle("Remove Picture")
                .setMessage("This picture will be deleted and cannot be undone.")
                .setCancelable(true)
                .setNegativeButton("Cancel", (dialogInterface, i) -> dialogInterface.dismiss())
                .setPositiveButton("Sure", (dialogInterface, i) -> {
                    selectedImages.remove(image);
                    adapter.notifyItemRemoved(pos);
                }).create().show();
    }

    @Override
    public void onBackPressed() {
        new AlertDialog.Builder(this)
                .setTitle("Discard")
                .setMessage("Changes made will be lost, sure to continue?")
                .setCancelable(true)
                .setNegativeButton("Cancel", (dialogInterface, i) -> dialogInterface.dismiss())
                .setPositiveButton("Continue", (dialogInterface, i) -> super.onBackPressed()).create().show();
    }
}