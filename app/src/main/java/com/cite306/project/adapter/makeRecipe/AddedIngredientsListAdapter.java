package com.cite306.project.adapter.makeRecipe;

import android.annotation.SuppressLint;
import android.content.ClipData;
import android.content.Context;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.cite306.project.R;
import com.cite306.project.dashboard.makeRecipe.MakeRecipeUpdateFragment;
import com.cite306.project.model.Ingredient;

import java.util.ArrayList;
import java.util.List;

public class AddedIngredientsListAdapter extends RecyclerView.Adapter< AddedIngredientsListAdapter.ViewHolder > {

    MakeRecipeUpdateFragment makeRecipeUpdateFragment;
    List< Ingredient > addedIngredients = new ArrayList<>();
    Context context;

    public AddedIngredientsListAdapter( Context context, List< Ingredient > addedIngredients, MakeRecipeUpdateFragment makeRecipeUpdateFragment ) {
        this.context = context;
        this.addedIngredients = addedIngredients;
        this.makeRecipeUpdateFragment = makeRecipeUpdateFragment;
    }

    public AddedIngredientsListAdapter(Context context, MakeRecipeUpdateFragment makeRecipeUpdateFragment) {
        this.context = context;
        this.makeRecipeUpdateFragment = makeRecipeUpdateFragment;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        final TextView ingredientListRowTitle, ingredientListRowAisle, ingredientListRowCost;

        final CardView ingredientListRowCardView;
        final ImageView ingredientListRowImage;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            ingredientListRowCardView = itemView.findViewById( R.id.ingredientListRowCardView );

            ingredientListRowTitle = itemView.findViewById( R.id.ingredientListRowTitle );
            ingredientListRowImage = itemView.findViewById( R.id.ingredientListRowImage );
            ingredientListRowAisle = itemView.findViewById( R.id.ingredientListRowAisle );
            ingredientListRowCost = itemView.findViewById( R.id.ingredientListRowCost );
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
        final int ingredientId = addedIngredients.get( position ).getId();
        final String ingredientName = addedIngredients.get( position ).getName();
        final String ingredientImageURL = addedIngredients.get( position ).getImageURL();
        final String ingredientAisle = addedIngredients.get( position ).getAisle();
        final float ingredientCost = addedIngredients.get( position ).getCost();

        holder.ingredientListRowTitle.setText( ingredientName );
        holder.ingredientListRowAisle.setText( ingredientAisle );
        holder.ingredientListRowCost.setText( ingredientCost + " pesos" );

        Glide.with( context ).load( ingredientImageURL ).transform( new CenterCrop(), new RoundedCorners( 20 ) ).into( holder.ingredientListRowImage );

        holder.ingredientListRowCardView.setOnLongClickListener( v -> {
            final ClipData recipeData = ClipData.newPlainText( "addedIngredient", String.valueOf( ingredientId ) );

            final View.DragShadowBuilder shadow = new View.DragShadowBuilder( holder.ingredientListRowCardView );
            v.startDrag( recipeData, shadow, null, 0 );

            return false;
        } );
    }

    @Override
    public int getItemCount() {
        return addedIngredients.size();
    }

    public void addNewIngredient( Ingredient ingredient ) {
        this.addedIngredients.add( ingredient );
    }

    public void removeIngredient( int id ) {
        List< Ingredient > newList = new ArrayList<>();

        for ( Ingredient ingredient : addedIngredients ) {
            if ( ingredient.getId() == id ) continue;
            newList.add( ingredient );
        }

        this.addedIngredients = newList;
    }

    public List< Ingredient > getAddedIngredients() {
        return addedIngredients;
    }

    public void setAddedIngredients( List< Ingredient > addedIngredients ) {
        this.addedIngredients = addedIngredients;
    }

    public float getEstimatedCost() {
        float estimatedCost = 0;
        for ( Ingredient addedIngredient : addedIngredients ) {
            estimatedCost += addedIngredient.getCost();
        }

        return estimatedCost;
    }
}
