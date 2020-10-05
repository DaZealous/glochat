package net.glochat.dev.fragment;


import android.view.View;

import net.glochat.dev.R;
import net.glochat.dev.base.BaseFragment;


public class PersonalHomeFragment extends BaseFragment implements View.OnClickListener {

    public static PersonalHomeFragment newInstance() {
        return new PersonalHomeFragment();
    }

    @Override
    public void onClick(View view) {

    }

    @Override
    protected void onViewCreated() {

    }

    @Override
    protected int setLayoutView() {
        return R.layout.fragment_personal_home;
    }
}
