package net.glochat.dev.fragment;

import net.glochat.dev.R;
import net.glochat.dev.base.BaseFragment;

public class ProfileFragment extends BaseFragment {

    public ProfileFragment() {
        // Required empty public constructor
    }


    public static ProfileFragment newInstance() {
        return new ProfileFragment();
    }


    @Override
    protected void onViewCreated() {

    }

    @Override
    protected int setLayoutView() {
        return R.layout.fragment_search;
    }


}