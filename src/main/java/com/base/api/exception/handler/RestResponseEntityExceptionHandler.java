package com.base.api.exception.handler;

import com.base.api.dto.ApiResponse;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.NonNull;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.util.stream.Collectors;

@ControllerAdvice
public class RestResponseEntityExceptionHandler extends ResponseEntityExceptionHandler {

    public RestResponseEntityExceptionHandler() {
        super();
    }

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(
        @NonNull MethodArgumentNotValidException ex,
        @NonNull HttpHeaders headers,
        @NonNull HttpStatusCode status,
        @NonNull WebRequest request) {

        logger.error("400 Status Code", ex);
        BindingResult result = ex.getBindingResult();

        String error = result.getAllErrors().stream()
            .map(e -> (e instanceof FieldError) ? ((FieldError) e).getField() + " : " + e.getDefaultMessage()
                : e.getObjectName() + " : " + e.getDefaultMessage())
            .collect(Collectors.joining(", "));

        return handleExceptionInternal(ex, new ApiResponse(false, error), headers, HttpStatus.BAD_REQUEST, request);
    }
}
