package store.sugyo.common.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import store.sugyo.common.dto.ErrorResponse;

import static store.sugyo.common.exception.GlobalErrorCode.INTERNAL_SERVER_ERROR;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ApplicationException.class)
    public ResponseEntity<ErrorResponse> handleApplicationException(ApplicationException ex) {
        ErrorCode errorCode = ex.getErrorCode();
        log.warn("ApplicationException occurred: {}", errorCode.getMessage());
        return new ResponseEntity<>(new ErrorResponse(errorCode), errorCode.getHttpStatus());
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGlobalException(Exception ex) {
        ErrorCode errorCode = GlobalErrorCode.from(ex);
        if (errorCode == INTERNAL_SERVER_ERROR) {
            log.error("Unknown exception occurred: {}", ex);
        } else {
            log.warn("Exception occurred: {} - {}", errorCode.getMessage(), ex.getMessage());
        }
        return new ResponseEntity<>(new ErrorResponse(errorCode), errorCode.getHttpStatus());
    }
}

