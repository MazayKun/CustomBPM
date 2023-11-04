package ru.mikheev.kirill.custombpm.common;

import lombok.AllArgsConstructor;
import lombok.Data;

import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;

@Data
@AllArgsConstructor
public class ErrorResponse {

    private String reasonPhrase;
    private String errorMessage;

    public static ErrorResponse internalServerError(String errorMessage) {
        return new ErrorResponse(INTERNAL_SERVER_ERROR.getReasonPhrase(), errorMessage);
    }
}
