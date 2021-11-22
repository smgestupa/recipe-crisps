package com.cite306.project.model;

public class Ingredient {

    int id;
    String name;
    String imageURL;
    String aisle;
    String amount;
    float cost;

    public Ingredient( int id, String name, String imageURL ) {
        this.id = id;
        this.name = name;
        this.imageURL = "https://spoonacular.com/cdn/ingredients_500x500/" + imageURL;
    }

    public Ingredient( int id, String name, String imageURL, String aisle, float cost ) {
        this.id = id;
        this.name = name;
        this.imageURL = "https://spoonacular.com/cdn/ingredients_500x500/" + imageURL;
        this.aisle = aisle;
        this.cost = cost;
    }

    public Ingredient( int id, String name, String imageURL, String aisle, int amount, String unit ) {
        this.id = id;
        this.name = name;
        this.imageURL = "https://spoonacular.com/cdn/ingredients_500x500/" + imageURL;
        this.aisle = aisle;
        this.amount = amount + " " + unit;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getImageURL() {
        return imageURL;
    }

    public String getAisle() {
        return aisle;
    }

    public String getAmount() {
        return amount;
    }

    public float getCost() {
        return cost;
    }
}
