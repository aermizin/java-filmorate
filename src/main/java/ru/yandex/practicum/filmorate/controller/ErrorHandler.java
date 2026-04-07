package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.yandex.practicum.filmorate.exception.DuplicatedDataException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestControllerAdvice
public class ErrorHandler {

   @ExceptionHandler
   @ResponseStatus(HttpStatus.CONFLICT)
   public Map<String, String> handleDuplicatedDataException(final DuplicatedDataException e) {
       return Map.of("error", e.getMessage());
   }

   @ExceptionHandler
   @ResponseStatus(HttpStatus.NOT_FOUND)
   public Map<String, String> handleNotFoundException(final NotFoundException e) {
       return Map.of("error", e.getMessage());
   }

   @ExceptionHandler
   @ResponseStatus(HttpStatus.BAD_REQUEST)
   public Map<String, String> handleValidationException(final ValidationException e) {
       return Map.of("error", e.getMessage());
   }

   @ExceptionHandler
   @ResponseStatus(HttpStatus.BAD_REQUEST)
   public Map<String, String> handleMethodArgumentNotValidException(final MethodArgumentNotValidException ex) {

       Map<String, String> errors = new HashMap<>();
       ex.getBindingResult().getAllErrors().forEach((error) -> {
           String fieldName = ((FieldError) error).getField();
           String errorMessage = error.getDefaultMessage();
           errors.put(fieldName, errorMessage);
       });
       log.warn("Ошибки валидации: {}", errors);
       return errors;
   }

   @ExceptionHandler
   @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
   public Map<String, String> handleInternalServerError(final Throwable e) {
       return Map.of("error", "Произошла непредвиденная ошибка.");
   }

}