package com.example.homeworkcrawler.exception;

import com.example.homeworkcrawler.controller.Response;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class ExceptionHandleControllerAdvice {

    @ExceptionHandler({JSoapDocumentTimeoutException.class, NotFoundJSoapDocumentException.class})
    protected ResponseEntity<Response<?>> crawlingException(Exception e) {
        log.warn(e.getMessage(), e);
        return new ResponseEntity<>(Response.error(e), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(Exception.class)
    protected ResponseEntity<Response<?>> commonException(Exception e) {
        log.error(e.getMessage(), e);
        return new ResponseEntity<>(Response.error(e), HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
