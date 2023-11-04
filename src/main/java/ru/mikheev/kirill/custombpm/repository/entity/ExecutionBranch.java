package ru.mikheev.kirill.custombpm.repository.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.util.UUID;

@Data
@Entity
@Table(name = "execution_branches")
public class ExecutionBranch {

    private static final String ROOT_BRANCH_NAME = "root";

    @Id
    private UUID id;

    private String code;

    private UUID processId;

    @Enumerated(EnumType.STRING)
    private BranchStatus status;

    private String currentBlockCode;

    public static ExecutionBranch rootBranch(UUID processId, String currentBlockCode) {
        ExecutionBranch result = new ExecutionBranch();
        result.setCode(ROOT_BRANCH_NAME);
        result.setProcessId(processId);
        result.setStatus(BranchStatus.IN_PROGRESS);
        result.setCurrentBlockCode(currentBlockCode);
        return result;
    }

    public static ExecutionBranch newBranch(ExecutionBranch originBranch, String currentBlockCode) {
        ExecutionBranch result = new ExecutionBranch();
        result.setCode(originBranch.getCode() + '_' + currentBlockCode);
        result.setProcessId(originBranch.getProcessId());
        result.setStatus(BranchStatus.IN_PROGRESS);
        result.setCurrentBlockCode(currentBlockCode);
        return result;
    }
}
