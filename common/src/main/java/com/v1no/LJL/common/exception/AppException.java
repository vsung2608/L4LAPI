package com.v1no.LJL.common.exception;

import lombok.*;

@EqualsAndHashCode(callSuper = true)
@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class AppException extends RuntimeException {
    private ErrorCode errorCode;
}
