package com.cite306.project.model;

import java.util.List;

public class Recipe {

    int id;
    String title;
    String imageURL;
    int servings;
    int readyIn;
    double pricePerServing;
    List< Ingredient > ingredients;

    public Recipe( int id, String title, String imageURL ) {
        this.id = id;
        this.title = title;
        this.imageURL = imageURL;
    }

    public Recipe( int id, String title, String imageURL, int servings, int readyIn, double pricePerServing, List< Ingredient > ingredients ) {
        this.id = id;
        this.title = title;
        this.imageURL = imageURL;
        this.servings = servings;
        this.readyIn = readyIn;
        this.pricePerServing = pricePerServing;
        this.ingredients = ingredients;
    }

    public int getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getImageURL() {
        return imageURL;
    }

    public int getServings() {
        return servings;
    }

    public int getReadyIn() {
        return readyIn;
    }

    public double getPricePerServing() {
        return pricePerServing;
    }

    public List<Ingredient> getIngredients() {
        return ingredients;
    }
}
