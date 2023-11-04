package ru.mikheev.kirill.custombpm.scheme.general;

import lombok.AllArgsConstructor;
import lombok.Data;
import ru.mikheev.kirill.custombpm.common.DataType;

@Data
@AllArgsConstructor
public class InitParameter {
    private String name;
    private String innerName;
    private DataType type;
    private String defaultValue;
}
