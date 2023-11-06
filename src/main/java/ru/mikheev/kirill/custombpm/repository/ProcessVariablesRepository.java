package ru.mikheev.kirill.custombpm.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.mikheev.kirill.custombpm.repository.entity.ProcessVariable;
import ru.mikheev.kirill.custombpm.repository.entity.ProcessVariableId;

import java.util.Collection;
import java.util.List;
import java.util.UUID;

@Repository
public interface ProcessVariablesRepository extends JpaRepository<ProcessVariable, ProcessVariableId> {

    @Query("select pv from ProcessVariable pv where pv.id.processId = ?1")
    List<ProcessVariable> findAllByProcessId(UUID processId);

    @Query("select pv from ProcessVariable pv where pv.id.processId = ?1 and pv.id.name in ?2")
    List<ProcessVariable> findAllByProcessIdAndNameIn(UUID processId, Collection<String> names);
}
