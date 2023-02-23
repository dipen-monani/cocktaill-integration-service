package com.cocktail.cocktail.controller;

import com.cocktail.cocktail.dto.CocktailResponse;
import com.cocktail.cocktail.service.CocktailService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/cocktail")
@Slf4j
public class CocktailController {

    @Autowired
    private CocktailService cocktailService;

    @GetMapping
    public ResponseEntity<List<CocktailResponse>> getCocktailsByIngredientNames(@RequestParam String ingredients) {
        log.info("Get Cocktails by ingredients request received.");
        List<CocktailResponse> responseList = cocktailService.getCocktailsByIngredientNames(ingredients);
        log.info("Get Cocktails by ingredients request completed.");
        return new ResponseEntity(responseList, HttpStatus.OK);
    }
}
