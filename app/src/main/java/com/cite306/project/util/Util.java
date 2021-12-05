package com.cite306.project.util;

public interface Util {

    // ADD YOUR SPOONACULAR API KEY HERE; YOU CAN GET IT BY REGISTERING HERE: https://spoonacular.com/food-api/console#Dashboard
    String spoonacularAPI_Key = "API_Key";
    String spoonacularRecipeSearchURL = "https://api.spoonacular.com/recipes/complexSearch?";
    String spoonacularIngredientSearchURL = "https://api.spoonacular.com/food/ingredients/search?";
    String spoonacularSpecificIngredientSearch = "https://api.spoonacular.com/food/ingredients/{id}/information/?amount=1&apiKey={apiKey}";
}
