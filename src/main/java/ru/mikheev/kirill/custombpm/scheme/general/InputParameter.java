package ru.mikheev.kirill.custombpm.scheme.general;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
public class InputParameter {
    private String name;
    private String innerName;
    private String type;
    private String defaultValue;
}
