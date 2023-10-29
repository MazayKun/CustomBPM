package ru.mikheev.kirill.custombpm.repository.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

import java.util.UUID;

@Data
@Entity
@Table(name = "branch_errors")
public class BranchError {

    @Id
    private UUID branchId;

    private String errorMessage;

    private String stackTrace;
}
