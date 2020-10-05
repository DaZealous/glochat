package net.glochat.dev.fragment;

import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import net.glochat.dev.R;
import net.glochat.dev.base.BaseFragment;

import java.util.ArrayList;

import butterknife.BindView;

public class MainFragment extends BaseFragment implements BottomNavigationView.OnNavigationItemSelectedListener, BottomNavigationView.OnNavigationItemReselectedListener {

    @BindView(R.id.bottom_navigation)
    BottomNavigationView bottomNavigationView;

    ArrayList<Fragment> fragments = new ArrayList<>();

    public MainFragment() {
        // Required empty public constructor
    }


    public static MainFragment newInstance() {
        return new MainFragment();
    }


    @Override
    protected void onViewCreated() {
        bottomNavigationView.setOnNavigationItemSelectedListener(this);
        bottomNavigationView.setOnNavigationItemReselectedListener(this);
        fragments.add(HomeFragment.newInstance());
        fragments.add(CallFragment.newInstance());
        fragments.add(PhotoFragment.newInstance());
        fragments.add(ChatFragment.newInstance());
        fragments.add(ProfileFragment.newInstance());

        loadFragment(fragments.get(0));
    }

    @Override
    protected int setLayoutView() {
        return R.layout.fragment_main;
    }

    private boolean loadFragment(Fragment fragment) {
        //switching fragment
        if (fragment != null) {
            getChildFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment_container, fragment)
                    .commit();
            return true;
        }
        return false;
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        Fragment fragment = new Fragment();
        switch (item.getItemId()) {
            case R.id.home_page:
                fragment = fragments.get(0);
                break;
            case R.id.search_page:
                fragment = fragments.get(1);
                break;
            case R.id.media_page:
                fragment = fragments.get(2);
                break;
            case R.id.chat_page:
                fragment = fragments.get(3);
                break;
            case R.id.profile_page:
                fragment = fragments.get(4);
                break;
        }
        return  loadFragment(fragment);
    }

    @Override
    public void onNavigationItemReselected(@NonNull MenuItem item) {

    }
}