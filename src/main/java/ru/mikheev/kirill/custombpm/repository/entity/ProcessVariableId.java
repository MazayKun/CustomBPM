package ru.mikheev.kirill.custombpm.repository.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.*;

import java.io.Serial;
import java.io.Serializable;
import java.util.UUID;

@Getter
@Embeddable
@NoArgsConstructor
@AllArgsConstructor
public class ProcessVariableId implements Serializable {

    @Serial
    private static final long serialVersionUID = -5575736844077798238L;

    @Column(name = "process_id")
    private UUID processId;

    @Column(name = "name")
    private String name;

}
