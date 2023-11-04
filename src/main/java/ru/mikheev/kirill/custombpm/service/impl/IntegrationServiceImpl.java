package ru.mikheev.kirill.custombpm.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import ru.mikheev.kirill.custombpm.common.ErrorResponse;
import ru.mikheev.kirill.custombpm.common.exception.CallEndpointException;
import ru.mikheev.kirill.custombpm.service.IntegrationService;
import ru.mikheev.kirill.custombpm.service.dto.CallEndpointCommand;
import ru.mikheev.kirill.custombpm.service.dto.CallEndpointRequest;

@Slf4j
@Service
@RequiredArgsConstructor
public class IntegrationServiceImpl implements IntegrationService {

    private static final ObjectMapper RESPONSE_MAPPER = new ObjectMapper();

    private final RestTemplate restTemplate;

    @Override
    public void callEndpoint(CallEndpointCommand callEndpointCommand) {
        ResponseEntity<String> response = restTemplate.postForEntity(
                callEndpointCommand.getEndpoint(),
                commandToRequest(callEndpointCommand),
                String.class
        );
        if (response.getStatusCode().isError()) {
            try {
                throw CallEndpointException.errorResponse(
                        RESPONSE_MAPPER.readValue(response.getBody(), ErrorResponse.class)
                );
            } catch (JsonProcessingException e) {
                log.error("Parse exception", e);
                throw CallEndpointException.parseErrorException();
            }
        }
    }

    private CallEndpointRequest commandToRequest(CallEndpointCommand callEndpointCommand) {
        return new CallEndpointRequest(
                callEndpointCommand.getProcessId(),
                callEndpointCommand.getBranchCode(),
                callEndpointCommand.getParameters()
        );
    }
}
