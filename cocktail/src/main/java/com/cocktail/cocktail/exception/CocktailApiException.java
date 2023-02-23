package com.cocktail.cocktail.exception;

import com.cocktail.cocktail.enums.ErrorCode;
import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonSetter;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;

@NoArgsConstructor
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CocktailApiException {

    private HttpStatus statusCode;

    private ErrorCode errorCode;

    private String message;

    private String description;

    public CocktailApiException(HttpStatus statusCode, ErrorCode errorCode) {
        this.statusCode = statusCode;
        this.errorCode = errorCode;
        this.message = "Unexpected error";
        this.description = "No error description provided";
    }

    public CocktailApiException(
            HttpStatus statusCode, ErrorCode errorCode, String message, String description) {
        this.statusCode = statusCode;
        this.errorCode = errorCode;
        this.message = message;
        this.description = description;
    }

    @JsonGetter("statusCode")
    public Integer getStatusCodeValue() {
        return statusCode.value();
    }

    @JsonSetter("statusCode")
    public void setStatusCodeValue(Integer statusCodeValue) {
        statusCode = HttpStatus.valueOf(statusCodeValue);
    }
}
