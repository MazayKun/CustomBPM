package ru.mikheev.kirill.custombpm.repository.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "branch_exceptions")
public class BranchException {

    @Id
    private UUID branchId;

    private String message;

    private String stackTrace;
}
