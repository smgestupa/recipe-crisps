package com.cite306.project.adapter.makeRecipe;

import android.annotation.SuppressLint;
import android.content.ClipData;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.cite306.project.R;
import com.cite306.project.dashboard.makeRecipe.MakeRecipeFragment;
import com.cite306.project.dashboard.makeRecipe.MakeRecipeInfoFragment;
import com.cite306.project.dashboard.makeRecipe.MakeRecipeUpdateFragment;
import com.cite306.project.dashboard.recipeSearch.RecipeSearchInfoFragment;
import com.cite306.project.model.Ingredient;

import java.util.List;

public class MakeRecipeAdapter extends RecyclerView.Adapter< MakeRecipeAdapter.ViewHolder > {

    MakeRecipeFragment makeRecipeFragment;
    List< String > savedRecipes;
    Context context;

    public MakeRecipeAdapter( Context context, List< String > savedRecipes, MakeRecipeFragment makeRecipeFragment ) {
        this.context = context;
        this.savedRecipes = savedRecipes;
        this.makeRecipeFragment = makeRecipeFragment;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        final TextView savedRecipeName;
        final CardView savedRecipeCardView;

        final Animation scaleUp, scaleDown;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            savedRecipeCardView = itemView.findViewById( R.id.savedRecipeCardView );
            savedRecipeName = itemView.findViewById( R.id.savedRecipeName );

            scaleUp = AnimationUtils.loadAnimation( context, R.anim.scale_up );
            scaleDown = AnimationUtils.loadAnimation( context, R.anim.scale_down );
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from( context );
        View view = inflater.inflate( R.layout.row_saved_recipe, parent, false );

        return new ViewHolder( view );
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.savedRecipeName.setText( savedRecipes.get( position ) );

        holder.savedRecipeCardView.setOnLongClickListener( v -> {
            updateRecipe( savedRecipes.get( position ) );
            return true;
        } );

        holder.savedRecipeCardView.setOnClickListener( v -> {
            holder.savedRecipeCardView.startAnimation( holder.scaleUp );
            holder.savedRecipeCardView.startAnimation( holder.scaleDown );
            fragmentJump( position );
        } );
    }

    @Override
    public int getItemCount() {
        return savedRecipes.size();
    }

    private void updateRecipe( String name ) {
        Fragment recipeUpdate = new MakeRecipeUpdateFragment();
        Bundle bundle = new Bundle();

        bundle.putString( "recipe_name", name );
        recipeUpdate.setArguments( bundle );

        FragmentTransaction transaction = makeRecipeFragment.getActivity().getSupportFragmentManager().beginTransaction();
        transaction.setCustomAnimations( R.anim.fragment_slide_up, R.anim.fragment_slide_down );
        transaction.add( R.id.makeRecipeActivity_fragment, recipeUpdate, "makeRecipeUpdate" );
        transaction.commit();
    }

    private void fragmentJump( int position ) {
        Fragment makeRecipeInfo = new MakeRecipeInfoFragment();
        Bundle bundle = new Bundle();

        bundle.putString( "recipe_name", savedRecipes.get( position ) );
        makeRecipeInfo.setArguments( bundle );

        FragmentTransaction transaction = makeRecipeFragment.getActivity().getSupportFragmentManager().beginTransaction();
        transaction.setCustomAnimations( R.anim.fragment_slide_up, R.anim.fragment_slide_down );
        transaction.add( R.id.makeRecipeActivity_fragment, makeRecipeInfo, "makeRecipeInfo" );
        transaction.commit();
    }
}
