package com.cocktail.cocktail.service;

import com.cocktail.cocktail.common.RestClient;
import com.cocktail.cocktail.dto.Cocktail;
import com.cocktail.cocktail.dto.CocktailDetails;
import com.cocktail.cocktail.dto.CocktailResponse;
import com.cocktail.cocktail.enums.ErrorCode;
import com.cocktail.cocktail.exception.CustomException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@Slf4j
public class CocktailService {

    @Autowired
    private RestClient restClient;

    @Autowired
    private ObjectMapper objectMapper;

    @Value("${cocktail.ingredients.filter.uri}")
    private String ingredientsFilterURL;

    @Value("${cocktail.detail.search.uri}")
    private String cocktailDetailSearchURL;

    public List<CocktailResponse> getCocktailsByIngredientNames(String ingredients) {
        if(StringUtils.isBlank(ingredients)){
            log.error("Ingredients is empty in request");
            throw new CustomException("Ingredients is empty in request", ErrorCode.REQUEST_NOT_VALID, HttpStatus.BAD_REQUEST);
        }
        log.info("Input ingredients {} ",ingredients);
        List<String> ingredientsList = Arrays.asList(ingredients.split(","));

        log.info("Starting process for getting cocktail details by ingredientsList");
        List<CocktailDetails.Drink> cocktailDetailsByIngredients = getCocktailDetailsByIngredients(ingredientsList);
        log.info("Finished process for getting cocktail details by ingredientsList");
        if (cocktailDetailsByIngredients.isEmpty()) {
            log.debug("No cocktail details found by ingredientsList hence returning emptyList.");
            return new ArrayList<>();
        }
        log.info("Starting process for getting cocktail details by name");
        List<Cocktail.DrinkDetailsDTO> cocktailDetailsByName = getCocktailDetailsByName(cocktailDetailsByIngredients, ingredientsList);
        log.info("Finished process for getting cocktail details by name");

        return mapToCocktailResponse(cocktailDetailsByName, ingredientsList);
    }

    private List<CocktailDetails.Drink> getCocktailDetailsByIngredients(List<String> ingredients) {
        CocktailDetails cocktailDetails = null;
        List<CocktailDetails.Drink> cocktails = new ArrayList<>();
        List<CocktailDetails.Drink> filterCocktails = new ArrayList<>();

        try {
            for (String ingredientName : ingredients) {
                log.debug("Making a rest call to get cocktail details by ingredient: {}", ingredientName);
                String ingredientsResponse = restClient.makeRestAPICall(ingredientName, ingredientsFilterURL, HttpMethod.GET);
                log.debug("Finished a rest call to get cocktail details by ingredient: {} and details is {}", ingredientName, ingredientsResponse);
                if (StringUtils.isNotEmpty(ingredientsResponse)) {
                    cocktailDetails = objectMapper.readValue(ingredientsResponse, CocktailDetails.class);
                    log.debug("Total no. of {} cocktail details found by {} ingredient", cocktailDetails.getDrinks(), ingredientName);
                }
                if (CollectionUtils.isNotEmpty(Collections.singleton(cocktailDetails)) && !cocktailDetails.getDrinks().isEmpty()) {
                    if (cocktails.isEmpty()) {
                        cocktails.addAll(cocktailDetails.getDrinks());
                    } else {
                        cocktailDetails.getDrinks().forEach(drink -> {
                            if (cocktails.contains(drink)) {
                                filterCocktails.add(drink);
                            }
                        });
                    }
                }
            }
            if (filterCocktails.isEmpty()) {
                filterCocktails.addAll(cocktails);
            }
            log.debug("Total no of filtered cocktails {}", filterCocktails.size());
            return filterCocktails;
        } catch (Exception e) {
            log.error("Exception while parsing rest call response: {}", e.getMessage(), e);
            throw new CustomException(String.format("Exception while making rest call: %s", e.getMessage()), ErrorCode.INTERNAL_SERVER_ERROR, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    private List<Cocktail.DrinkDetailsDTO> getCocktailDetailsByName(List<CocktailDetails.Drink> filterCocktails, List<String> ingredients) {
        List<Cocktail.DrinkDetailsDTO> cocktailDetails = new ArrayList<>();
        try {
            for (CocktailDetails.Drink cocktail : filterCocktails) {
                log.debug("Making a rest call to get cocktail details by name: {}", cocktail.getDrinkName());
                String cocktailResponse = restClient.makeRestAPICall(cocktail.getDrinkName(), cocktailDetailSearchURL, HttpMethod.GET);
                log.debug("Finished a rest call to get cocktail details by name: {} and response is {}", cocktail.getDrinkName(), cocktailResponse);
                if (StringUtils.isNotEmpty(cocktailResponse)) {
                    Cocktail cocktailDrink = objectMapper.readValue(cocktailResponse, Cocktail.class);
                    cocktailDetails.addAll(fetchCocktailMatchingAllIngredient(cocktailDrink.getDrinks(), ingredients));
                }
            }
            log.debug("Total no. of cocktails filter by names {}", cocktailDetails.size());
            return cocktailDetails;
        } catch (Exception e) {
            log.error("Exception while parsing rest call response: {}", e.getMessage(), e);
            throw new CustomException(String.format("Exception while making rest call: %s", e.getMessage()), ErrorCode.INTERNAL_SERVER_ERROR, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    private List<CocktailResponse> mapToCocktailResponse(List<Cocktail.DrinkDetailsDTO> cocktailDetails, List<String> ingredients) {
        log.info("Process map to cocktail object with size: {}", cocktailDetails.size());
        List<CocktailResponse> cocktailResponseList = new ArrayList<>();

        cocktailDetails.forEach(cocktail -> {
            log.info("Starting map to object for {}", cocktail.getDrinkName());

            List<CocktailResponse.Ingredients> ingredientWithMeasure = getAllIngredientsWithMeasure(cocktail);
            log.info("Total no. of ingredient with measure {}, for cocktail {}", ingredientWithMeasure.size(), cocktail.getDrinkName());

            List<String> missingIngredients = getMissingIngredients(ingredientWithMeasure, ingredients);
            log.info("Total no. of missing ingredients {}, for cocktail {}", missingIngredients.size(), cocktail.getDrinkName());

            cocktailResponseList.add(CocktailResponse.builder()
                    .cocktail(cocktail.getDrinkName())
                    .instructions(cocktail.getDrinkInstructions())
                    .ingredients(ingredientWithMeasure)
                    .missingIngredients(missingIngredients)
                    .build());
        });
        log.info("Finished process to getting cocktails with cocktail size:{} ", cocktailResponseList.size());
        return cocktailResponseList;
    }

    private List<String> getMissingIngredients(List<CocktailResponse.Ingredients> ingredientsList, List<String> ingredients) {
        List<String> lowerCaseIngredients = ingredients.stream().map(String::toLowerCase).collect(Collectors.toList());
        return ingredientsList.stream().map(CocktailResponse.Ingredients::getName)
                .filter(name -> !lowerCaseIngredients.contains(name.toLowerCase()))
                .collect(Collectors.toList());
    }

    private List<CocktailResponse.Ingredients> getAllIngredientsWithMeasure(Cocktail.DrinkDetailsDTO p) {
        List<CocktailResponse.Ingredients> ingredientWithMeasure = new ArrayList<>();
        ingredientWithMeasure.add(getIndividualIngredientsWithMeasure(p.getIngredient1(), p.getMeasure1()));
        ingredientWithMeasure.add(getIndividualIngredientsWithMeasure(p.getIngredient2(), p.getMeasure2()));
        ingredientWithMeasure.add(getIndividualIngredientsWithMeasure(p.getIngredient3(), p.getMeasure3()));
        ingredientWithMeasure.add(getIndividualIngredientsWithMeasure(p.getIngredient4(), p.getMeasure4()));
        ingredientWithMeasure.add(getIndividualIngredientsWithMeasure(p.getIngredient5(), p.getMeasure5()));
        ingredientWithMeasure.add(getIndividualIngredientsWithMeasure(p.getIngredient6(), p.getMeasure6()));
        ingredientWithMeasure.add(getIndividualIngredientsWithMeasure(p.getIngredient7(), p.getMeasure7()));
        ingredientWithMeasure.add(getIndividualIngredientsWithMeasure(p.getIngredient8(), p.getMeasure8()));
        ingredientWithMeasure.add(getIndividualIngredientsWithMeasure(p.getIngredient9(), p.getMeasure9()));
        ingredientWithMeasure.add(getIndividualIngredientsWithMeasure(p.getIngredient10(), p.getMeasure10()));
        ingredientWithMeasure.add(getIndividualIngredientsWithMeasure(p.getIngredient11(), p.getMeasure11()));
        ingredientWithMeasure.add(getIndividualIngredientsWithMeasure(p.getIngredient12(), p.getMeasure12()));
        ingredientWithMeasure.add(getIndividualIngredientsWithMeasure(p.getIngredient13(), p.getMeasure13()));
        ingredientWithMeasure.add(getIndividualIngredientsWithMeasure(p.getIngredient14(), p.getMeasure14()));
        ingredientWithMeasure.add(getIndividualIngredientsWithMeasure(p.getIngredient15(), p.getMeasure15()));
        return ingredientWithMeasure.stream().filter(Objects::nonNull).collect(Collectors.toList());
    }

    private CocktailResponse.Ingredients getIndividualIngredientsWithMeasure(String ingredient, String measure) {
        if (StringUtils.isNotEmpty(ingredient) && StringUtils.isNotEmpty(measure)) {
            return CocktailResponse.Ingredients.builder()
                    .name(ingredient)
                    .measure(measure)
                    .build();
        }
        return null;
    }

    private List<Cocktail.DrinkDetailsDTO> fetchCocktailMatchingAllIngredient(List<Cocktail.DrinkDetailsDTO> drinks, List<String> ingredients) {
        log.info("Filter cocktails with matching ingredients");
        List<Cocktail.DrinkDetailsDTO> matchIngredientCocktails = new ArrayList<>();
        List<String> lowerCaseIngredients = ingredients.stream().map(String::toLowerCase).collect(Collectors.toList());

        for (Cocktail.DrinkDetailsDTO drink : drinks) {
            List<String> cocktailIngredientList = Arrays.asList(drink.getIngredient1(),
                    drink.getIngredient2(),
                    drink.getIngredient3(),
                    drink.getIngredient4(),
                    drink.getIngredient5(),
                    drink.getIngredient6(),
                    drink.getIngredient7(),
                    drink.getIngredient8(),
                    drink.getIngredient9(),
                    drink.getIngredient10(),
                    drink.getIngredient11(),
                    drink.getIngredient12(),
                    drink.getIngredient13(),
                    drink.getIngredient14(),
                    drink.getIngredient15());
            List<String> lowerCaseIngredientList = cocktailIngredientList.stream().filter(Objects::nonNull).map(String::toLowerCase).collect(Collectors.toList());
            if (lowerCaseIngredientList.containsAll(lowerCaseIngredients)) {
                matchIngredientCocktails.add(drink);
            }
        }
        return matchIngredientCocktails;
    }
}
