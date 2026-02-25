package com.v1no.LJL.common.dto;
import java.time.LocalDateTime;

import org.springframework.http.HttpStatus;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApiResponse<T> {
    private final int status;
    private final String message;
    private final T data;
    private final LocalDateTime timestamp;

    public static <T> ApiResponse<T> ok(T data) {
        return ApiResponse.<T>builder()
            .status(HttpStatus.OK.value())
            .message("Success")
            .data(data)
            .timestamp(LocalDateTime.now())
            .build();
    }

    public static <T> ApiResponse<T> created(T data) {
        return ApiResponse.<T>builder()
            .status(HttpStatus.CREATED.value())
            .message("Created")
            .data(data)
            .timestamp(LocalDateTime.now())
            .build();
    }
}
