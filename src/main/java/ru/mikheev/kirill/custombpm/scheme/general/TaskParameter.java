package ru.mikheev.kirill.custombpm.scheme.general;

import lombok.AllArgsConstructor;
import lombok.Data;
import ru.mikheev.kirill.custombpm.common.DataType;

import java.util.Objects;

@Data
@AllArgsConstructor
public class TaskParameter {

    private String name;
    private DataType type;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TaskParameter that = (TaskParameter) o;
        return Objects.equals(name, that.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }
}
