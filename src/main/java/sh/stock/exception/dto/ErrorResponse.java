package sh.stock.exception.dto;

import sh.stock.exception.ErrorCode;

import java.time.LocalDateTime;

public record ErrorResponse(LocalDateTime timestamp, int status, String code, String message) {

    public static ErrorResponse of(ErrorCode errorCode, String message) {
        return new ErrorResponse(
                LocalDateTime.now(),
                errorCode.getStatus().value(),
                errorCode.getCode(),
                message
        );
    }
}
