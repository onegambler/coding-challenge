package com.n26.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.springframework.http.HttpStatus.BAD_REQUEST;

import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import org.junit.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.context.request.WebRequest;

public class ExceptionHandlerTest {

    private ExceptionHandler exceptionHandler = new ExceptionHandler();

    @Test
    public void handleMethodArgumentNotValidShouldAlwaysReturnUnprocessableEntity() {
        ResponseEntity<Object> response = exceptionHandler.handleMethodArgumentNotValid(null, null, null, null);
        assertThat(response.getStatusCodeValue()).isEqualTo(422);

        response = exceptionHandler.handleMethodArgumentNotValid(
            mock(MethodArgumentNotValidException.class),
            new HttpHeaders(),
            BAD_REQUEST,
            mock(WebRequest.class));
        assertThat(response.getStatusCodeValue()).isEqualTo(422);
    }


    @Test
    public void handleHttpMessageNotReadableShouldReturnUnprocessableEntityWhenCauseInvalidFormatException() {
        final ResponseEntity<Object> response = exceptionHandler.handleHttpMessageNotReadable(
            new HttpMessageNotReadableException("something happened", mock(InvalidFormatException.class)),
            new HttpHeaders(),
            BAD_REQUEST,
            mock(WebRequest.class));

        assertThat(response.getStatusCodeValue()).isEqualTo(422);

    }


    @Test
    public void handleHttpMessageNotReadableShouldReturnBadRequestWhenCauseNotInvalidFormatException() {
        final ResponseEntity<Object> response = exceptionHandler.handleHttpMessageNotReadable(
            new HttpMessageNotReadableException("something happened", mock(RuntimeException.class)),
            new HttpHeaders(),
            BAD_REQUEST,
            mock(WebRequest.class));
        assertThat(response.getStatusCodeValue()).isEqualTo(400);
    }
}