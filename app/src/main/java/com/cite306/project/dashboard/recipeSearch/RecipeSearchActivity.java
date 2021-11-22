package com.cite306.project.dashboard.recipeSearch;

import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import com.cite306.project.R;

public class RecipeSearchActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView( R.layout.activity_recipesearch );

        getSupportFragmentManager().beginTransaction().add( R.id.recipeSearchActivity_fragment, new RecipeSearchFragment(), "recipeSearchFragment" ).commit();
    }
}
