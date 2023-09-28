package ru.mikheev.kirill.custombpm.scheme;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Setter
@Getter
public class Task {
    private String description;
    private String code;
    private String type;
    private Endpoint endpoint;
    private List<Parameter> inputParameters;
    private List<Parameter> outputParameters;
}
