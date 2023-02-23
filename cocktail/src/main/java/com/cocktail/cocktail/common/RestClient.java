package com.cocktail.cocktail.common;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
public class RestClient {

    @Autowired
    private RestTemplate restTemplate;


    public String makeRestAPICall(String inputParam, String url, HttpMethod methodType) {
        return restTemplate.exchange(String.format(url, inputParam), methodType, null, String.class).getBody();
    }
}
