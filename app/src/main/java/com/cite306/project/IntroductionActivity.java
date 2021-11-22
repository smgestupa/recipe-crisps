package com.cite306.project;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.os.Bundle;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

import com.airbnb.lottie.LottieAnimationView;
import com.cite306.project.main.OnboardingFragment1;
import com.cite306.project.main.OnboardingFragment2;
import com.cite306.project.main.OnboardingFragment3;

public class IntroductionActivity extends AppCompatActivity {

    ImageView logo, title, splashBg;
    LottieAnimationView lottieAnimationView;

    private static final int NUM_PAGES = 3;
    private ViewPager viewPager;
    private ScreenSlidePagerAdapter pagerAdapter;
    Animation anim;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView( R.layout.activity_introductory );

        logo = findViewById( R.id.logo_intro );
        title = findViewById(R.id.title);
        splashBg = findViewById( R.id.imgPasta );
        lottieAnimationView = findViewById( R.id.lottie );

        viewPager = findViewById( R.id.pager );
        pagerAdapter = new ScreenSlidePagerAdapter(getSupportFragmentManager());
        viewPager.setAdapter( pagerAdapter );

        anim = AnimationUtils.loadAnimation(this, R.anim.o_b_anim);
        viewPager.startAnimation( anim );
        splashBg.animate().translationY(-1800).setDuration(1000).setStartDelay(4000);
        logo.animate().translationY(1400).setDuration(1000).setStartDelay(4000);
        title.animate().translationY(1400).setDuration(1000).setStartDelay(4000);
        lottieAnimationView.animate().translationY(1500).setDuration(1000).setStartDelay(4000);
    }

    private class ScreenSlidePagerAdapter extends FragmentStatePagerAdapter{

        public ScreenSlidePagerAdapter(@NonNull FragmentManager fm) {
            super(fm);
        }

        @NonNull
        @Override
        public Fragment getItem( int position ) {
            switch( position ){
                case 0: return new OnboardingFragment1();
                case 1: return new OnboardingFragment2();
                case 2: return new OnboardingFragment3();
            }

            return new OnboardingFragment3();
        }

        @Override
        public int getCount() {
            return NUM_PAGES;
        }
    }
}
