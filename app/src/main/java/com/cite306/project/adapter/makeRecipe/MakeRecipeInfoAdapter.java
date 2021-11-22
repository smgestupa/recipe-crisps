package com.cite306.project.adapter.makeRecipe;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.cite306.project.R;
import com.cite306.project.dashboard.makeRecipe.MakeRecipeInfoFragment;
import com.cite306.project.model.Ingredient;

import java.util.List;

public class MakeRecipeInfoAdapter extends RecyclerView.Adapter< MakeRecipeInfoAdapter.ViewHolder > {

    MakeRecipeInfoFragment makeRecipeInfoFragment;
    List< Ingredient > recipeIngredients;
    Context context;

    public MakeRecipeInfoAdapter(Context context, List< Ingredient > recipeIngredients, MakeRecipeInfoFragment makeRecipeInfoFragment) {
        this.context = context;
        this.recipeIngredients = recipeIngredients;
        this.makeRecipeInfoFragment = makeRecipeInfoFragment;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        final TextView recipeIngredientTitle, recipeIngredientAisle, recipeIngredientCost;
        final ImageView recipeIngredientImage;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            recipeIngredientImage = itemView.findViewById( R.id.ingredientListRowImage );
            recipeIngredientTitle = itemView.findViewById( R.id.ingredientListRowTitle );
            recipeIngredientAisle = itemView.findViewById( R.id.ingredientListRowAisle );
            recipeIngredientCost = itemView.findViewById( R.id.ingredientListRowCost );
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from( context );
        View view = inflater.inflate( R.layout.row_ingredient_list, parent, false );

        return new ViewHolder( view );
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.recipeIngredientTitle.setText( recipeIngredients.get( position ).getName() );
        holder.recipeIngredientAisle.setText( recipeIngredients.get( position ).getAisle() );
        holder.recipeIngredientCost.setText( recipeIngredients.get( position ).getCost() + " pesos" );

        Glide.with( context ).load( recipeIngredients.get( position ).getImageURL() ).transform( new CenterCrop(), new RoundedCorners( 20 ) ).into( holder.recipeIngredientImage );
    }

    @Override
    public int getItemCount() {
        return recipeIngredients.size();
    }

    public float getEstimatedCost() {
        float estimatedCost = 0;
        for ( Ingredient recipeIngredient : recipeIngredients ) {
            estimatedCost += recipeIngredient.getCost();
        }

        return estimatedCost;
    }
}
