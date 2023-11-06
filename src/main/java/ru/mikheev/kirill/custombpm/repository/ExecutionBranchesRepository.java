package ru.mikheev.kirill.custombpm.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.mikheev.kirill.custombpm.repository.entity.ExecutionBranch;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ExecutionBranchesRepository extends JpaRepository<ExecutionBranch, UUID> {

    Optional<ExecutionBranch> findByCodeAndProcessId(String code, UUID processId); // TODO: Под вопросом, возможно лучше по id

    @Modifying
    @Query("update ExecutionBranch eb set eb.currentBlockCode = ?3 where eb.code = ?1 and eb.processId = ?2")
    void nextTask(String branchCode, UUID processId, String nextTaskCode);

    @Modifying
    @Query("update ExecutionBranch eb set eb.status = ru.mikheev.kirill.custombpm.repository.entity.BranchStatus.FINISHED, eb.currentBlockCode = ?3 where eb.code = ?1 and eb.processId = ?2")
    void finishBranch(String branchCode, UUID processId, String finishTaskCode);

    @Modifying
    @Query("update ExecutionBranch eb set eb.status = ru.mikheev.kirill.custombpm.repository.entity.BranchStatus.GATE_PASS, eb.currentBlockCode = ?3 where eb.code = ?1 and eb.processId = ?2")
    void gatePassed(String branchCode, UUID processId, String gateTaskCode);

    @Query("select eb from ExecutionBranch eb where eb.processId = ?1 and eb.status = ru.mikheev.kirill.custombpm.repository.entity.BranchStatus.IN_PROGRESS")
    List<ExecutionBranch> findUnfinishedBranches(UUID processId);

    List<ExecutionBranch> findAllByProcessId(UUID processId);

    @Query("select eb from ExecutionBranch eb where eb.processId = ?1 and eb.currentBlockCode = ?2 and eb.status = ru.mikheev.kirill.custombpm.repository.entity.BranchStatus.GATE_PASS")
    List<ExecutionBranch> findAllBranchesThatPassedGate(UUID processId, String gateCode);
}
