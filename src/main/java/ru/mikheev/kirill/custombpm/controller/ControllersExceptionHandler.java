package ru.mikheev.kirill.custombpm.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import ru.mikheev.kirill.custombpm.common.ErrorResponse;

@Slf4j
@ControllerAdvice
public class ControllersExceptionHandler {

    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(Exception.class)
    public ErrorResponse handleGeneralException(Exception e) {
        logException(e);

        return ErrorResponse.internalServerError(e.getMessage());
    }

    private void logException(Exception e) {
        log.error(e.getMessage(), e);
    }
}
