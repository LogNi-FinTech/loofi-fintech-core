package com.logni.account.exception;

import com.logni.account.exception.account.AccountCreationExp;
import com.logni.account.exception.transaction.TxnValidationException;
import com.logni.account.utils.AccountErrors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.context.request.WebRequest;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@ControllerAdvice
public class GlobalExceptionHandler {
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public @ResponseBody
    ErrorResponse handleValidationExceptions(
            MethodArgumentNotValidException ex, WebRequest webRequest) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });
        log.error("Exception",ex);
        return new ErrorResponse(AccountErrors.getErrorCode(AccountErrors.ACCOUNT_SERVICE,AccountErrors.INVALID_INPUT),
                errors.toString());

    }
    /*
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MyBadDataException.class)
    @ResponseBody ErrorInfo
    handleBadRequest(HttpServletRequest req, Exception ex) {
        return new ErrorInfo(req.getRequestURL(), ex);
    }
    */

    @ExceptionHandler({Exception.class,RuntimeException.class})
    public final ResponseEntity<ErrorResponse> handleAllExceptions(Exception ex) {
        log.error("Exception",ex);
        ErrorResponse error = new ErrorResponse(AccountErrors.INTERNAL_ERROR, ex.getMessage());
        return new ResponseEntity<>(error, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler({AccountCreationExp.class})
    public final ResponseEntity<ErrorResponse> handleAccountCreation(AccountCreationExp ex) {
        log.error("Exception",ex);
        ErrorResponse error = new ErrorResponse(AccountErrors.INTERNAL_ERROR, ex.getMessage());
        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler({CommonException.class})
    public final ResponseEntity<ErrorResponse> handleCommonException(CommonException ex) {
        log.error("Exception",ex);
        ErrorResponse error = new ErrorResponse(ex.getErrorCode(), ex.getMessage());
        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler({TxnValidationException.class})
    public final ResponseEntity<ErrorResponse> handleValidation(TxnValidationException ex) {
        log.error("Exception",ex);
        ErrorResponse error = new ErrorResponse(ex.getErrorCode(), ex.getMessage());
        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }

}
