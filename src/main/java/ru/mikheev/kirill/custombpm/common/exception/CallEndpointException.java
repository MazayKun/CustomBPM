package ru.mikheev.kirill.custombpm.common.exception;

import ru.mikheev.kirill.custombpm.common.ErrorResponse;

public class CallEndpointException extends RuntimeException {

    public CallEndpointException(String message) {
        super(message);
    }

    public static CallEndpointException errorResponse(ErrorResponse errorResponse) {
        return new CallEndpointException("Error during endpoint call " + errorResponse);
    }

    public static CallEndpointException parseErrorException() {
        return new CallEndpointException("Cannot parse error response body");
    }
}
