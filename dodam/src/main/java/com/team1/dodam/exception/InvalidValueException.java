package com.team1.dodam.exception;

import com.team1.dodam.global.error.ErrorCode;

/* 잘못된 값이 사용되었을 때 사용*/
public class InvalidValueException extends BusinessException {
    public InvalidValueException(ErrorCode errorCode) {
        super(errorCode);
    }
}
