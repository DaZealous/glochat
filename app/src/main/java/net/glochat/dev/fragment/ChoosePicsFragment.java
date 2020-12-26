package net.glochat.dev.fragment;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.CursorLoader;
import androidx.loader.content.Loader;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import net.glochat.dev.EditPicturePostActivity;
import net.glochat.dev.R;
import net.glochat.dev.adapter.ChoosePicAdapter;
import net.glochat.dev.view.ChoosePicView;

import java.util.ArrayList;

public class ChoosePicsFragment extends BottomSheetDialogFragment implements LoaderManager.LoaderCallbacks<Cursor>, ChoosePicView {

    public ChoosePicsFragment() {
        // Required empty public constructor
    }

    private BottomSheetBehavior bottomSheetBehavior;
    private AppBarLayout appBarLayout;
    private ArrayList<String> listOfAllImages = new ArrayList<>();
    private ArrayList<String> selectedImages = new ArrayList<>();
    private ChoosePicAdapter adapter;
    private RecyclerView recyclerView;
    private static final int IMAGE_LOADER_ID = 1;
    private Button btnNext;
    private TextView textCounter;


    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        BottomSheetDialog bottomSheet = (BottomSheetDialog) super.onCreateDialog(savedInstanceState);

        View view = View.inflate(getContext(), R.layout.choose_pic_fragment_layout, null);

        ImageButton cancelBtn = view.findViewById(R.id.choose_pic_frag_cancel_btn);
        btnNext = view.findViewById(R.id.choose_pic_frag_btn_next);
        textCounter = view.findViewById(R.id.selectedImgCounter);
        appBarLayout = view.findViewById(R.id.choose_pic_frag_appBarLayout);
        recyclerView = view.findViewById(R.id.choose_pic_frag_recycler_view);

        bottomSheet.setOnShowListener(dialogInterface -> setupFullHeight(bottomSheet));

        initRecyclerViews();

        bottomSheet.setContentView(view);
        /*bottomSheetBehavior = BottomSheetBehavior.from((View) (view.getParent()));
        //setting Peek at the 16:9 ratio keyline of its parent.
        bottomSheetBehavior.setPeekHeight(BottomSheetBehavior.PEEK_HEIGHT_AUTO);*/

        cancelBtn.setOnClickListener(view12 -> dismiss());

        btnNext.setOnClickListener(view1 -> {
            startActivity(new Intent(requireContext(), EditPicturePostActivity.class).putExtra("selectedImages", selectedImages));
            if (getActivity() != null)
                getActivity().finish();
        });

        return bottomSheet;
    }

    private void initRecyclerViews() {
        adapter = new ChoosePicAdapter(requireContext(), listOfAllImages, this);
        recyclerView.setLayoutManager(new GridLayoutManager(requireContext(), 4));
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(adapter);
        getLoaderManager().initLoader(IMAGE_LOADER_ID, null, this);
    }

    private int getPhoneHeight() {
        return Resources.getSystem().getDisplayMetrics().heightPixels;
    }

    @NonNull
    @Override
    public Loader<Cursor> onCreateLoader(int id, @Nullable Bundle args) {
        Uri uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
        String[] projection = new String[]{MediaStore.MediaColumns.DATA, MediaStore.Images.Media.BUCKET_DISPLAY_NAME};
        String selection = null;    //Selection criteria
        String[] selectionArgs = new String[]{};  //Selection criteria
        String sortOrder = null;

        return new CursorLoader(
                requireContext().getApplicationContext(),
                uri,
                projection,
                selection,
                selectionArgs,
                sortOrder);
    }

    @Override
    public void onLoadFinished(@NonNull Loader<Cursor> loader, Cursor data) {
        int index = data.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA);
        while (data.moveToNext()) {
            listOfAllImages.add(data.getString(index));
        }
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onLoaderReset(@NonNull Loader<Cursor> loader) {

    }

    @Override
    public void onStart() {
        super.onStart();
        if (bottomSheetBehavior != null)
            bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
    }

    private void hideAppBar(View view) {
        ViewGroup.LayoutParams params = view.getLayoutParams();
        params.height = 0;
        view.setLayoutParams(params);

    }

    private void showAppBar(View view, int size) {
        ViewGroup.LayoutParams params = view.getLayoutParams();
        params.height = size;
        view.setLayoutParams(params);
    }

    private int getActionBarSize() {
        final TypedArray array = getContext().getTheme().obtainStyledAttributes(new int[]{android.R.attr.actionBarSize});
        int size = (int) array.getDimension(0, 0);
        return size;
    }

    @Override
    public boolean imageExist(String image) {
        return selectedImages.contains(image);
    }

    @Override
    public void addImage(String image) {
        selectedImages.add(image);
        setCounter(selectedImages.size());
        setBtnEnabled(selectedImages.size());
    }

    private void setBtnEnabled(int size) {
        if (size <= 0) {
            btnNext.setEnabled(false);
            btnNext.setAlpha(0.5f);
        } else {
            btnNext.setEnabled(true);
            btnNext.setAlpha(1f);
        }
    }

    private void setCounter(int size) {
        if (size <= 0) {
            textCounter.setVisibility(View.GONE);
            textCounter.setText("");
        } else {
            textCounter.setVisibility(View.VISIBLE);
            textCounter.setText(String.format("%s", size));
        }
    }

    @Override
    public void removeImage(String image) {
        selectedImages.remove(image);
        setCounter(selectedImages.size());
        setBtnEnabled(selectedImages.size());
    }

    private void setupFullHeight(BottomSheetDialog bottomSheetDialog) {
        FrameLayout bottomSheet = (FrameLayout) bottomSheetDialog.findViewById(R.id.design_bottom_sheet);
        bottomSheetBehavior = BottomSheetBehavior.from(bottomSheet);
        ViewGroup.LayoutParams layoutParams = bottomSheet.getLayoutParams();

        int windowHeight = getWindowHeight();
        if (layoutParams != null) {
            layoutParams.height = windowHeight;
        }
        bottomSheet.setLayoutParams(layoutParams);
        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);

        bottomSheetBehavior.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View view, int i) {
                if (BottomSheetBehavior.STATE_EXPANDED == i) {
                    showAppBar(appBarLayout, getActionBarSize());
                }
                if (BottomSheetBehavior.STATE_COLLAPSED == i) {
                    hideAppBar(appBarLayout);
                }

                if (BottomSheetBehavior.STATE_HIDDEN == i) {
                    dismiss();
                }

            }

            @Override
            public void onSlide(@NonNull View view, float v) {

            }
        });

    }

    private int getWindowHeight() {
        // Calculate window height for fullscreen use
        DisplayMetrics displayMetrics = new DisplayMetrics();
        ((Activity) getContext()).getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        return displayMetrics.heightPixels;
    }
}