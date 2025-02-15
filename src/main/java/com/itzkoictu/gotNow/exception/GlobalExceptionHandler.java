package com.itzkoictu.gotNow.exception;

import jakarta.persistence.EntityExistsException;
import jakarta.validation.ConstraintViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;


@ControllerAdvice
public class GlobalExceptionHandler {


//    @ExceptionHandler(ConstraintViolationException.class)
//    public ResponseEntity<String> handleEmailInvalid(ConstraintViolationException e){
//        String message = e.getMessage();
//        message= message.substring(message.lastIndexOf("[")+1);
//        return  new ResponseEntity<>("Error: "+ message, HttpStatus.CONFLICT);
//    }
    @ExceptionHandler(Exception.class)
        public ResponseEntity<String> handleAlreadyExist(Exception e) {
        if(e instanceof MethodArgumentNotValidException){
                    String message = e.getMessage();
        message= message.substring(message.lastIndexOf("[")+1, message.lastIndexOf("]")-1);
        return  new ResponseEntity<>("Error: "+ message, HttpStatus.CONFLICT);
        }
        return new ResponseEntity<>("Error: "+e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
    }

}
