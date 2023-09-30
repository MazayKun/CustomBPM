package ru.mikheev.kirill.custombpm.scheme.general;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class InitParameter {
    private String name;
    private String innerName;
    private String type;
    private String defaultValue;
}
