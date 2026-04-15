package org.fujitsu.codes.etms.exception;

import java.time.LocalDateTime;
import java.util.List;

import org.fujitsu.codes.etms.model.dto.ApiResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import jakarta.servlet.http.HttpServletRequest;

@RestControllerAdvice
public class GlobalRestExceptionHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(GlobalRestExceptionHandler.class);

    @ExceptionHandler(DataNotFoundException.class)
    public ResponseEntity<ApiResponse<Object>> handleNotFound(DataNotFoundException ex, HttpServletRequest req) {
        LOGGER.warn("Resource not found on {}: {}", req.getRequestURI(), ex.getMessage());
        return build(HttpStatus.NOT_FOUND, ex.getMessage(), null);
    }

    @ExceptionHandler(InvalidInputException.class)
    public ResponseEntity<ApiResponse<Object>> handleInvalidInput(InvalidInputException ex, HttpServletRequest req) {
        LOGGER.warn("Invalid request on {}: {}", req.getRequestURI(), ex.getMessage());
        return build(HttpStatus.BAD_REQUEST, ex.getMessage(), List.of(ex.getMessage()));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<Object>> handleValidation(MethodArgumentNotValidException ex, HttpServletRequest req) {
        List<String> errors = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(error -> error.getField() + ": " + error.getDefaultMessage())
                .toList();
        LOGGER.warn("Validation failed on {}: {}", req.getRequestURI(), errors);
        return build(HttpStatus.BAD_REQUEST, "Validation failed", errors);
    }

    @ExceptionHandler(BindException.class)
    public ResponseEntity<ApiResponse<Object>> handleBind(BindException ex, HttpServletRequest req) {
        List<String> errors = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(error -> error.getField() + ": " + error.getDefaultMessage())
                .toList();
        LOGGER.warn("Binding failed on {}: {}", req.getRequestURI(), errors);
        return build(HttpStatus.BAD_REQUEST, "Invalid request", errors);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Object>> handleGeneric(Exception ex, HttpServletRequest req) {
        LOGGER.error("Unhandled exception on {}", req.getRequestURI(), ex);
        return build(HttpStatus.INTERNAL_SERVER_ERROR, "Internal server error", List.of(ex.getMessage()));
    }

    private ResponseEntity<ApiResponse<Object>> build(HttpStatus status, String message, Object details) {
        ApiResponse<Object> body = new ApiResponse<>();
        body.setSuccess(false);
        body.setMessage(message);
        body.setData(details);
        body.setTimestamp(LocalDateTime.now());
        return ResponseEntity.status(status).body(body);
    }
}
