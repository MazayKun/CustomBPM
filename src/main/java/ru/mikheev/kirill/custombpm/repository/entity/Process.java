package ru.mikheev.kirill.custombpm.repository.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.util.UUID;

@Data
@Entity
@Table(name = "processes")
public class Process {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Enumerated(EnumType.STRING)
    private ProcessStatus status;

    private String type;

    public static Process newProcess(String type) {
        Process result = new Process();
        result.setStatus(ProcessStatus.IN_PROGRESS);
        result.setType(type);
        return result;
    }
}
