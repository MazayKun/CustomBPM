package ru.mikheev.kirill.custombpm.service.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CallEndpointRequest {
    private UUID processId;
    private String branchId;
    private Map<String, String> parameters;
}
