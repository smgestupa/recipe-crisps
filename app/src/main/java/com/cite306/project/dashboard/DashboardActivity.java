package com.cite306.project.dashboard;

import android.os.Bundle;
import android.widget.LinearLayout;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import com.cite306.project.R;
import com.cite306.project.dashboard.DashboardFragment;
import com.cite306.project.dashboard.makeRecipe.MakeRecipeFragment;
import com.cite306.project.dashboard.makeRecipe.MakeRecipeUpdateFragment;

public class DashboardActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView( R.layout.activity_dashboard );

        getSupportFragmentManager().beginTransaction().add( R.id.dashboard_fragment, new DashboardFragment(), "dashboardFragment" ).commit();
    }
}
