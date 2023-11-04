package ru.mikheev.kirill.custombpm.repository.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import ru.mikheev.kirill.custombpm.common.DataType;

import java.util.UUID;

@Data
@Entity
@NoArgsConstructor
@Table(name = "process_variables")
public class ProcessVariable {

    @Getter(AccessLevel.NONE)
    @EmbeddedId
    private ProcessVariableId id;

    @Enumerated(EnumType.STRING)
    private DataType type;

    private String value;

    public ProcessVariable(UUID processId, String name, DataType type, String value) {
        this.id = new ProcessVariableId(processId, name);
        this.type = type;
        this.value = value;
    }

    public UUID getProcessId() {
        return id.getProcessId();
    }

    public String getName() {
        return id.getName();
    }
}
