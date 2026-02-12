package com.v1no.LJL.common.exception;

import org.springframework.http.HttpStatus;

import lombok.Getter;

@Getter
public enum ErrorCode {
    EMAIL_EXISTED(1001, "Email đã tồn tại. Vui lòng chọn email khác!", HttpStatus.BAD_REQUEST),
    OLD_PASSWORD_INVALID(1002, "Mật khẩu cũ không hợp lệ. Vui lòng kiểm tra lại!", HttpStatus.BAD_REQUEST),
    INVALID_EMAIL_PASSWORD(1003, "Tài khoản hoặc mật khẩu không chính xác. Vui lòng kiểm tra lại", HttpStatus.BAD_REQUEST),
    INVALID_TOKEN(1004, "Token không hợp lệ", HttpStatus.UNAUTHORIZED),
    INCCORECT_OLDPASSWORLD(1005, "Mật khẩu cũ không chính xác", HttpStatus.BAD_REQUEST)
    ;

    private final int code;
    private final String message;
    private final HttpStatus status;

    ErrorCode(int code, String message, HttpStatus status) {
        this.code = code;
        this.message = message;
        this.status = status;
    }
}
