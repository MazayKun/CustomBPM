package ru.mikheev.kirill.custombpm.scheme.general;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
public class OutputParameter {
    private String name;
    private String innerName;
    private String type;
}
