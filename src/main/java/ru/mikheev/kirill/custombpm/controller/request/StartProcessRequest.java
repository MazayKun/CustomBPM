package ru.mikheev.kirill.custombpm.controller.request;

import lombok.Data;

import java.util.Map;

@Data
public class StartProcessRequest {
    private String schemeName;
    private Map<String, String> parameters;
}
