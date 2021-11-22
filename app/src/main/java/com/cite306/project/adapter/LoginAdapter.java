package com.cite306.project.adapter;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import com.cite306.project.main.ForgotFragment;
import com.cite306.project.main.LoginFragment;
import com.cite306.project.main.RegisterFragment;

public class LoginAdapter extends FragmentPagerAdapter {

    private Context context;
    int totalTabs;

    public LoginAdapter(FragmentManager fm, Context context, int totalTabs){
        super(fm);
        this.context = context;
        this.totalTabs = totalTabs;
    }

    @Override
    public int getCount() {
        return totalTabs;
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        switch ( position ){
            case 1: return new RegisterFragment();
            case 2: return new ForgotFragment();
            default: return new LoginFragment();
        }
    }
}
