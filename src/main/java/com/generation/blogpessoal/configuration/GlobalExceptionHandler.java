package com.generation.blogpessoal.configuration;

import java.util.ArrayList;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.server.ResponseStatusException;

import com.generation.blogpessoal.dto.ErrorResponse;
import com.generation.blogpessoal.dto.ErrorResponse.FieldError;

import jakarta.persistence.EntityNotFoundException;

@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * Trata erros de validação de campos (@Valid, @NotBlank, @Size, etc)
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationException(
            MethodArgumentNotValidException ex,
            WebRequest request) {

        List<FieldError> fieldErrors = new ArrayList<>();

        ex.getBindingResult().getFieldErrors().forEach(error ->
            fieldErrors.add(new FieldError(
                error.getField(),
                error.getDefaultMessage()
            ))
        );

        ErrorResponse errorResponse = new ErrorResponse(
            HttpStatus.BAD_REQUEST.value(),
            "Erro de Validação",
            "Há erros de validação no corpo da requisição"
        );
        errorResponse.setFields(fieldErrors);
        errorResponse.setPath(request.getDescription(false).replace("uri=", ""));

        return ResponseEntity
            .status(HttpStatus.BAD_REQUEST)
            .body(errorResponse);
    }

    /**
     * Trata erros de ResponseStatusException (lançados pelos controllers)
     */
    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<ErrorResponse> handleResponseStatusException(
            ResponseStatusException ex,
            WebRequest request) {

        ErrorResponse errorResponse = new ErrorResponse(
            ex.getStatusCode().value(),
            ex.getStatusCode().toString(),
            ex.getReason()
        );
        errorResponse.setPath(request.getDescription(false).replace("uri=", ""));

        return ResponseEntity
            .status(ex.getStatusCode())
            .body(errorResponse);
    }

    /**
     * Trata erros de entidade não encontrada
     */
    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleEntityNotFoundException(
            EntityNotFoundException ex,
            WebRequest request) {

        ErrorResponse errorResponse = new ErrorResponse(
            HttpStatus.NOT_FOUND.value(),
            "Recurso Não Encontrado",
            ex.getMessage()
        );
        errorResponse.setPath(request.getDescription(false).replace("uri=", ""));

        return ResponseEntity
            .status(HttpStatus.NOT_FOUND)
            .body(errorResponse);
    }

    /**
     * Trata exceções genéricas não capturadas
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGlobalException(
            Exception ex,
            WebRequest request) {

        ErrorResponse errorResponse = new ErrorResponse(
            HttpStatus.INTERNAL_SERVER_ERROR.value(),
            "Erro Interno do Servidor",
            "Ocorreu um erro inesperado. Por favor, tente novamente mais tarde."
        );
        errorResponse.setPath(request.getDescription(false).replace("uri=", ""));

        // Log do erro para fins de debugging
        System.err.println("Erro não tratado: " + ex.getMessage());
        ex.printStackTrace();

        return ResponseEntity
            .status(HttpStatus.INTERNAL_SERVER_ERROR)
            .body(errorResponse);
    }
}
