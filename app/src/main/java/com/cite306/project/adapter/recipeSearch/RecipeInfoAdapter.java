package com.cite306.project.adapter.recipeSearch;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.cite306.project.R;
import com.cite306.project.dashboard.recipeSearch.RecipeSearchInfoFragment;
import com.cite306.project.model.Ingredient;

import java.util.List;

public class RecipeInfoAdapter extends RecyclerView.Adapter< RecipeInfoAdapter.ViewHolder > {

    RecipeSearchInfoFragment recipeSearchInfoFragment;
    List< Ingredient > recipeIngredients;
    Context context;

    public RecipeInfoAdapter( Context context, List< Ingredient > recipeIngredients, RecipeSearchInfoFragment recipeSearchInfoFragment) {
        this.context = context;
        this.recipeIngredients = recipeIngredients;
        this.recipeSearchInfoFragment = recipeSearchInfoFragment;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        final TextView recipeIngredientTitle, recipeIngredientAisle, recipeIngredientAmount;

        final CardView recipeIngredientCardView;
        final ImageView recipeIngredientImage;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            recipeIngredientCardView = itemView.findViewById( R.id.ingredientListRowCardView );
            recipeIngredientImage = itemView.findViewById( R.id.recipeIngredientImage );

            recipeIngredientTitle = itemView.findViewById( R.id.recipeIngredientTitle );
            recipeIngredientAisle = itemView.findViewById( R.id.recipeIngredientAisle );
            recipeIngredientAmount = itemView.findViewById( R.id.recipeIngredientAmount );
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from( context );
        View view = inflater.inflate( R.layout.row_recipe_ingredient, parent, false );

        return new ViewHolder( view );
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.recipeIngredientTitle.setText( recipeIngredients.get( position ).getName() );
        holder.recipeIngredientAisle.setText( recipeIngredients.get( position ).getAisle() );
        holder.recipeIngredientAmount.setText( recipeIngredients.get( position ).getAmount() );

        Glide.with( context ).load( recipeIngredients.get( position ).getImageURL() ).transform( new CenterCrop(), new RoundedCorners( 20 ) ).into( holder.recipeIngredientImage );
    }

    @Override
    public int getItemCount() {
        return recipeIngredients.size();
    }
}
