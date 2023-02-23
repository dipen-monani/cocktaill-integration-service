package com.cocktail.cocktail.exception.handler;

import com.cocktail.cocktail.enums.ErrorCode;
import com.cocktail.cocktail.exception.CocktailApiException;
import com.cocktail.cocktail.exception.CustomException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@Slf4j
@ControllerAdvice
@Order
public class CocktailRestExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(CustomException.class)
    public final ResponseEntity<Object> handleAllExceptions(CustomException ex) {
        log.error("Handling general exception", ex);
        CocktailApiException apiError =
                new CocktailApiException(
                        ex.getHttpStatus(),
                        ex.getErrorCode(),
                        ex.getMessage(), "");
        return buildResponseEntity(apiError);
    }

    @ExceptionHandler({Exception.class})
    public final ResponseEntity<Object> handleAllExceptions(Exception ex) {
        log.error("Handling general exception", ex);
        CocktailApiException apiError =
                new CocktailApiException(
                        HttpStatus.INTERNAL_SERVER_ERROR,
                        ErrorCode.INTERNAL_SERVER_ERROR,
                        "Internal Server Error",
                        ex.getMessage());
        return buildResponseEntity(apiError);
    }

    private ResponseEntity<Object> buildResponseEntity(CocktailApiException apiError) {
        return new ResponseEntity<>(apiError, apiError.getStatusCode());
    }
}
