package com.cocktail.cocktail.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Cocktail {

    private List<DrinkDetailsDTO> drinks;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class DrinkDetailsDTO {
        @JsonProperty("idDrink")
        private String drinkId;
        @JsonProperty("strDrink")
        private String drinkName;
        @JsonProperty("strInstructions")
        private String drinkInstructions;
        @JsonProperty("strIngredient1")
        private String ingredient1;
        @JsonProperty("strIngredient2")
        private String ingredient2;
        @JsonProperty("strIngredient3")
        private String ingredient3;
        @JsonProperty("strIngredient4")
        private String ingredient4;
        @JsonProperty("strIngredient5")
        private String ingredient5;
        @JsonProperty("strIngredient6")
        private String ingredient6;
        @JsonProperty("strIngredient7")
        private String ingredient7;
        @JsonProperty("strIngredient8")
        private String ingredient8;
        @JsonProperty("strIngredient9")
        private String ingredient9;
        @JsonProperty("strIngredient10")
        private String ingredient10;
        @JsonProperty("strIngredient11")
        private String ingredient11;
        @JsonProperty("strIngredient12")
        private String ingredient12;
        @JsonProperty("strIngredient13")
        private String ingredient13;
        @JsonProperty("strIngredient14")
        private String ingredient14;
        @JsonProperty("strIngredient15")
        private String ingredient15;
        @JsonProperty("strMeasure1")
        private String measure1;
        @JsonProperty("strMeasure2")
        private String measure2;
        @JsonProperty("strMeasure3")
        private String measure3;
        @JsonProperty("strMeasure4")
        private String measure4;
        @JsonProperty("strMeasure5")
        private String measure5;
        @JsonProperty("strMeasure6")
        private String measure6;
        @JsonProperty("strMeasure7")
        private String measure7;
        @JsonProperty("strMeasure8")
        private String measure8;
        @JsonProperty("strMeasure9")
        private String measure9;
        @JsonProperty("strMeasure10")
        private String measure10;
        @JsonProperty("strMeasure11")
        private String measure11;
        @JsonProperty("strMeasure12")
        private String measure12;
        @JsonProperty("strMeasure13")
        private String measure13;
        @JsonProperty("strMeasure14")
        private String measure14;
        @JsonProperty("strMeasure15")
        private String measure15;
    }
}
