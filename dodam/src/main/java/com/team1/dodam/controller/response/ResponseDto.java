package com.team1.dodam.controller.response;

import com.team1.dodam.global.error.ErrorCode;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ResponseDto<T> {
  private boolean success;
  private T data;
  private ErrorCode errorCode;

  public static <T> ResponseDto<T> success(T data) {
    return new ResponseDto<>(true, data, ErrorCode.SUCCESS);
  }

  public static <T> ResponseDto<T> fail(ErrorCode errorCode) {
    return new ResponseDto<>(false, null, errorCode);
  }


}
