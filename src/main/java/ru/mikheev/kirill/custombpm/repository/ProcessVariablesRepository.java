package ru.mikheev.kirill.custombpm.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.mikheev.kirill.custombpm.repository.entity.ProcessVariable;
import ru.mikheev.kirill.custombpm.repository.entity.ProcessVariableId;

import java.util.List;
import java.util.UUID;

@Repository
public interface ProcessVariablesRepository extends JpaRepository<ProcessVariable, ProcessVariableId> {

    List<ProcessVariable> findAllByProcessId(UUID processId);

    List<ProcessVariable> findAllByProcessIdAndNameIn(UUID processId, List<String> names);
}
