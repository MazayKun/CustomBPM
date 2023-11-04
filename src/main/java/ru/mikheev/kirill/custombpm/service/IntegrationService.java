package ru.mikheev.kirill.custombpm.service;

import ru.mikheev.kirill.custombpm.service.dto.CallEndpointCommand;

public interface IntegrationService {

    void callEndpoint(CallEndpointCommand callEndpointCommand);

}
