package ru.mikheev.kirill.custombpm.scheme;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class Parameter {
    private String name;
    private String innerName;
    private String type;
    private String defaultValue;
}
