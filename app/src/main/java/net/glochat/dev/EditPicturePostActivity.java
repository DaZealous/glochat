package net.glochat.dev;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.theartofdev.edmodo.cropper.CropImage;

import net.glochat.dev.adapter.SelectedPicEditAdapter;
import net.glochat.dev.view.SelectedPicEditView;

import java.util.ArrayList;

public class EditPicturePostActivity extends AppCompatActivity implements SelectedPicEditView {

    private SelectedPicEditAdapter adapter;
    private ImageView imageDisplay;
    private ArrayList<String> selectedImages = null;
    private int prevPos = 0, currentPos = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_picture_post);

        RecyclerView recyclerView = findViewById(R.id.edit_picture_post_recycler_view);
        imageDisplay = findViewById(R.id.edit_picture_post_image_display);
        ImageButton btnBack = findViewById(R.id.edit_picture_post_btn_back);
        ImageButton btnCrop = findViewById(R.id.edit_picture_post_btn_crop);
        FloatingActionButton btnNext = findViewById(R.id.edit_picture_post_fab_continue);

        if (getIntent().getStringArrayListExtra("selectedImages") != null)
            selectedImages = getIntent().getStringArrayListExtra("selectedImages");
        adapter = new SelectedPicEditAdapter(this, selectedImages, this);
        Glide.with(this).load(selectedImages.get(0)).into(imageDisplay);
        LinearLayoutManager manager = new LinearLayoutManager(this);
        manager.setOrientation(LinearLayoutManager.HORIZONTAL);
        recyclerView.setLayoutManager(manager);
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(adapter);
        adapter.notifyDataSetChanged();

        if (selectedImages.size() <= 1)
            recyclerView.setVisibility(View.GONE);
        else
            recyclerView.setVisibility(View.VISIBLE);

        btnBack.setOnClickListener(view -> onBackPressed());

        btnCrop.setOnClickListener(view -> CropImage.activity(Uri.parse(selectedImages.get(currentPos)))
                .setAspectRatio(2, 2)
                .setAutoZoomEnabled(true)
                .setMinCropWindowSize(500, 500)
                .start(this));

        btnNext.setOnClickListener(view -> {
            startActivity(new Intent(this, PostPicWithCap.class).putExtra("selectedImages", selectedImages));
            finish();
        });
    }

    @Override
    public void selectImage(String image, int newPos) {
        Glide.with(this).load(image).into(imageDisplay);
        prevPos = currentPos;
        currentPos = newPos;
    }

    @Override
    public int getPrevPos() {
        return prevPos;
    }

    @Override
    public void removeImage(String image, int pos) {
        new AlertDialog.Builder(this)
                .setTitle("Remove Picture")
                .setMessage("This picture will be deleted and cannot be undone.")
                .setNegativeButton("Cancel", (dialogInterface, i) -> dialogInterface.dismiss())
                .setPositiveButton("Sure", (dialogInterface, i) -> {
                    selectedImages.remove(image);
                    adapter.notifyItemRemoved(pos);
                }).create().show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            if (resultCode == RESULT_OK && data != null) {
                Uri uri = CropImage.getActivityResult(data).getUri();
                Glide.with(this).load(uri).into(imageDisplay);
                selectedImages.remove(currentPos);
                selectedImages.add(currentPos, uri.toString());
                adapter.notifyDataSetChanged();
            }
        }
    }

    @Override
    public void onBackPressed() {
        new AlertDialog.Builder(this)
                .setTitle("Discard?")
                .setMessage("All changes will be lost.")
                .setNegativeButton("Cancel", (dialogInterface, i) -> dialogInterface.dismiss())
                .setPositiveButton("Discard", (dialogInterface, i) -> {
                    super.onBackPressed();
                }).create().show();
    }
}