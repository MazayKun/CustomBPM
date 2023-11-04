package ru.mikheev.kirill.custombpm.service.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Map;
import java.util.UUID;

@Data
@AllArgsConstructor
public class StartProcessDto {
    private UUID processId;
    private String branchCode;
    private Map<String, Object> processVariables;
}
