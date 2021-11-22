package com.cite306.project.adapter.recipeSearch;

import android.annotation.SuppressLint;
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
import com.cite306.project.dashboard.recipeSearch.RecipeSearchInfoFragment;
import com.cite306.project.dashboard.recipeSearch.RecipeSearchFragment;
import com.cite306.project.model.Recipe;

import java.util.List;

public class RecipeSearchAdapter extends RecyclerView.Adapter< RecipeSearchAdapter.ViewHolder > {

    RecipeSearchFragment recipeSearchFragment;
    List<Recipe> recipeQueries;
    Context context;

    public RecipeSearchAdapter(Context context, List<Recipe> recipeQueries, RecipeSearchFragment recipeSearchFragment ) {
        this.context = context;
        this.recipeQueries = recipeQueries;
        this.recipeSearchFragment = recipeSearchFragment;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        final CardView recipeCardView;
        final TextView recipeTitle;
        final ImageView recipeImage;

        final Animation scaleUp, scaleDown;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            recipeCardView = itemView.findViewById( R.id.ingredientListRowCardView );
            recipeTitle = itemView.findViewById( R.id.searchedIngredientTitle );
            recipeImage = itemView.findViewById( R.id.searchedIngredientImage );

            scaleUp = AnimationUtils.loadAnimation( context, R.anim.scale_up );
            scaleDown = AnimationUtils.loadAnimation( context, R.anim.scale_down );
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from( context );
        View view = inflater.inflate( R.layout.row_recipe, parent, false );

        return new ViewHolder( view );
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.recipeTitle.setText( recipeQueries.get( position ).getTitle() );
        Glide.with( context ).load( recipeQueries.get( position ).getImageURL() ).transform( new CenterCrop(), new RoundedCorners( 20 ) ).into( holder.recipeImage );

        holder.recipeCardView.setOnClickListener( v -> {
            holder.recipeCardView.startAnimation( holder.scaleUp );
            holder.recipeCardView.startAnimation( holder.scaleDown );

            fragmentJump( position );
        } );
    }

    @Override
    public int getItemCount() {
        return recipeQueries.size();
    }

    private void fragmentJump( int position ) {
        Fragment recipeInfo = new RecipeSearchInfoFragment();
        Bundle bundle = new Bundle();

        bundle.putInt( "recipe_id", recipeQueries.get( position ).getId() );
        recipeInfo.setArguments( bundle );

        FragmentTransaction transaction = recipeSearchFragment.getActivity().getSupportFragmentManager().beginTransaction();
        transaction.setCustomAnimations( R.anim.fragment_slide_up, R.anim.fragment_slide_down );
        transaction.add( R.id.recipeSearch_fragment, recipeInfo, "recipeInfo" );
        transaction.commit();
    }
}
