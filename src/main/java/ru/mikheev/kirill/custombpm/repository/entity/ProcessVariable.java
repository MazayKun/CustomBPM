package ru.mikheev.kirill.custombpm.repository.entity;

import jakarta.persistence.*;
import lombok.Data;
import ru.mikheev.kirill.custombpm.common.DataType;

import java.util.UUID;

@Data
@Entity
@Table(name = "process_variables")
public class ProcessVariable {

    @EmbeddedId
    private ProcessVariableId id;

    @Column(name = "process_id", insertable = false, updatable = false)
    private UUID processId;

    @Column(name = "name", insertable = false, updatable = false)
    private String name;

    @Enumerated(EnumType.STRING)
    private DataType type;

    private String value;
}
