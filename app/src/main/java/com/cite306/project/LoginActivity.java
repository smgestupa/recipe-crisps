package com.cite306.project;

import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;
import com.cite306.project.adapter.LoginAdapter;
import com.cite306.project.main.LoginFragment;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;

public class LoginActivity extends AppCompatActivity {

    TabLayout tableLayout;
    ViewPager viewPager;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_login );

        tableLayout = findViewById(R.id.tab_layout);
        viewPager = findViewById(R.id.view_pager);

        tableLayout.addTab( tableLayout.newTab().setText( "Login" ) );
        tableLayout.addTab( tableLayout.newTab().setText( "Register" ) );
        tableLayout.addTab( tableLayout.newTab().setText( "Forgot" ) );
        tableLayout.setTabGravity( TabLayout.GRAVITY_CENTER );

        final LoginAdapter adapter = new LoginAdapter( getSupportFragmentManager(), this, tableLayout.getTabCount() );
        viewPager.setAdapter(adapter);

        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(
                tableLayout));
        tableLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });


    }
}
