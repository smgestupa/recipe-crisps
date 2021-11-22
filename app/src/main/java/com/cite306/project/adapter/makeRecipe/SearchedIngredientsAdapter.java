package com.cite306.project.adapter.makeRecipe;

import android.annotation.SuppressLint;
import android.content.ClipData;
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
import com.cite306.project.dashboard.makeRecipe.MakeRecipeUpdateFragment;
import com.cite306.project.model.Ingredient;

import java.util.List;

public class SearchedIngredientsAdapter extends RecyclerView.Adapter< SearchedIngredientsAdapter.ViewHolder > {

    MakeRecipeUpdateFragment makeRecipeUpdateFragment;
    List< Ingredient > ingredients;
    Context context;

    public SearchedIngredientsAdapter(Context context, List< Ingredient > ingredients, MakeRecipeUpdateFragment makeRecipeUpdateFragment) {
        this.context = context;
        this.ingredients = ingredients;
        this.makeRecipeUpdateFragment = makeRecipeUpdateFragment;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        final TextView searchedIngredientTitle;

        final CardView searchedIngredientCardView;
        final ImageView searchedIngredientImage;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            searchedIngredientCardView = itemView.findViewById( R.id.searchedIngredientCardView );
            searchedIngredientImage = itemView.findViewById( R.id.searchedIngredientImage );

            searchedIngredientTitle = itemView.findViewById( R.id.searchedIngredientTitle );
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from( context );
        View view = inflater.inflate( R.layout.row_searched_ingredients, parent, false );

        return new ViewHolder( view );
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        final int ingredientId = ingredients.get( position ).getId();
        final String ingredientName = ingredients.get( position ).getName();
        final String ingredientImageURL = ingredients.get( position ).getImageURL();

        holder.searchedIngredientTitle.setText( ingredientName );

        Glide.with( context ).load( ingredientImageURL ).transform( new CenterCrop(), new RoundedCorners( 20 ) ).into( holder.searchedIngredientImage );

        holder.searchedIngredientCardView.setOnLongClickListener( v -> {
            final ClipData recipeData = ClipData.newPlainText( String.valueOf( ingredientId ), "" );

            final View.DragShadowBuilder shadow = new View.DragShadowBuilder( holder.searchedIngredientCardView );
            v.startDrag( recipeData, shadow, null, 0 );

            return false;
        } );
    }

    @Override
    public int getItemCount() {
        return ingredients.size();
    }
}
