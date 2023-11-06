package ru.mikheev.kirill.custombpm.controller.request;

import lombok.Data;

import java.util.Map;
import java.util.UUID;

@Data
public class ProcessCallbackRequest {
    private UUID processId;
    private String branchCode;
    private Map<String, String> parameters;
}
