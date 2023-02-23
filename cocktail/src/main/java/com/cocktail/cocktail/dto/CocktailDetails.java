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
public class CocktailDetails {

    private List<Drink> drinks;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Drink {
        @JsonProperty("idDrink")
        private String drinkId;
        @JsonProperty("strDrinkThumb")
        private String drinkThumb;
        @JsonProperty("strDrink")
        private String drinkName;
    }
}
