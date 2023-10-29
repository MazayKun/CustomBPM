package ru.mikheev.kirill.custombpm.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.mikheev.kirill.custombpm.repository.entity.ExecutionBranch;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface ExecutionBranchesRepository extends JpaRepository<ExecutionBranch, UUID> {

    Optional<ExecutionBranch> findByCodeAndProcessId(String code, UUID processId); // TODO: Под вопросом, возможно лучше по id

}
