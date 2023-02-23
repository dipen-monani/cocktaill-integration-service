package com.cocktail.cocktail.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CocktailResponse {

    private String cocktail;
    private List<String> missingIngredients;
    private String instructions;
    private List<Ingredients> ingredients;

    @Data
    @Builder
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class Ingredients {
        private String name;
        private String measure;
    }
}
