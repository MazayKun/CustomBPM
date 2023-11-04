package ru.mikheev.kirill.custombpm.scheme.general;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import ru.mikheev.kirill.custombpm.common.DataType;

@Setter
@Getter
@AllArgsConstructor
public class OutputParameter {
    private String name;
    private String innerName;
    private DataType type;
}
