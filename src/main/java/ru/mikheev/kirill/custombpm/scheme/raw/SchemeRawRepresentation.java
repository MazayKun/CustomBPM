package ru.mikheev.kirill.custombpm.scheme.raw;

import lombok.Data;

import java.util.List;

@Data
public class SchemeRawRepresentation {
    private List<ParameterType> parameterTypes;
    private Start start;
    private List<Task> tasks;
    private List<Link> links;
    private List<Finish> finishes;
}
