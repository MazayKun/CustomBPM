package ru.mikheev.kirill.custombpm.service.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Map;
import java.util.UUID;

@Data
@AllArgsConstructor
public class CallEndpointCommand {
    private UUID processId;
    private String branchCode;
    private String endpoint;
    private Map<String, String> parameters;
}
