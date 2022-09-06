package com.team1.dodam.exception.advice;

import com.team1.dodam.dto.response.ResponseDto;
import com.team1.dodam.exception.BusinessException;
import com.team1.dodam.global.error.ErrorCode;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class CustomExceptionHandler {

  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseEntity<ResponseDto> handleValidationExceptions(MethodArgumentNotValidException exception) {
//    String errorMessage = exception.getBindingResult()
//        .getAllErrors()
//        .get(0)
//        .getDefaultMessage();

    return new ResponseEntity<>(ResponseDto.fail(ErrorCode.NOT_PASS_VALIDATION), HttpStatus.valueOf(ErrorCode.NOT_PASS_VALIDATION.getStatus()));
  }

  @ExceptionHandler(BusinessException.class)
  public ResponseEntity<ResponseDto> handleBusinessExceptions(BusinessException exception) {

    return new ResponseEntity<>(ResponseDto.fail(ErrorCode.UPLOAD_FAILED), HttpStatus.valueOf(ErrorCode.UPLOAD_FAILED.getStatus()));
  }

}
