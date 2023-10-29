package ru.mikheev.kirill.custombpm.repository.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.util.UUID;

@Data
@Entity
@Table(name = "execution_branches")
public class ExecutionBranch {

    @Id
    private UUID id;

    private String code;

    private UUID processId;

    @Enumerated(EnumType.STRING)
    private BranchStatus status;

    private String currentBlockCode;
}
