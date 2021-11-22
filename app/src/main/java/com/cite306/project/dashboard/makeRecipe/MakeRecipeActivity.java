package com.cite306.project.dashboard.makeRecipe;

import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import com.cite306.project.R;
import com.cite306.project.dashboard.recipeSearch.RecipeSearchFragment;

public class MakeRecipeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView( R.layout.activity_makerecipe );

        getSupportFragmentManager().beginTransaction().add( R.id.makeRecipeActivity_fragment, new MakeRecipeFragment(), "makeRecipe" ).commit();
    }
}
