package ru.mikheev.kirill.custombpm.scheme.general;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Objects;

@Data
@AllArgsConstructor
public class TaskParameter {

    private String innerName;
    private String type;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TaskParameter that = (TaskParameter) o;
        return Objects.equals(innerName, that.innerName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(innerName);
    }
}
