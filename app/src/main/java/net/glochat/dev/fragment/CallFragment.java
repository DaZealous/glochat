package net.glochat.dev.fragment;

import net.glochat.dev.R;
import net.glochat.dev.base.BaseFragment;

public class CallFragment extends BaseFragment {

    public CallFragment() {
        // Required empty public constructor
    }


    public static CallFragment newInstance() {
        return new CallFragment();
    }


    @Override
    protected void onViewCreated() {

    }

    @Override
    protected int setLayoutView() {
        return R.layout.fragment_search;
    }


}