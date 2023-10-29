package ru.mikheev.kirill.custombpm.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.mikheev.kirill.custombpm.repository.entity.BranchError;

import java.util.UUID;

@Repository
public interface BranchErrorsRepository extends JpaRepository<BranchError, UUID> {
}
