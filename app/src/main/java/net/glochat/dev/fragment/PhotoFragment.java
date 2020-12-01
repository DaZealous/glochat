package net.glochat.dev.fragment;

import net.glochat.dev.R;
import net.glochat.dev.base.BaseFragment;

public class PhotoFragment extends BaseFragment {

    public PhotoFragment() {
        // Required empty public constructor
    }


    public static PhotoFragment newInstance() {
        return new PhotoFragment();
    }

    @Override
    protected void onViewCreated() {

    }

    @Override
    protected int setLayoutView() {
        return R.layout.fragment_user_photo;
    }


}