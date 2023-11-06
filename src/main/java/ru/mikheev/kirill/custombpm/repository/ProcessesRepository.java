package ru.mikheev.kirill.custombpm.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.mikheev.kirill.custombpm.repository.entity.Process;

import java.util.UUID;

@Repository
public interface ProcessesRepository extends JpaRepository<Process, UUID> {

    @Modifying
    @Query("update Process p set p.status = ru.mikheev.kirill.custombpm.repository.entity.ProcessStatus.ERROR where p.id = ?1")
    void terminateProcessWithError(UUID processId);

    @Modifying
    @Query("update Process p set p.status = ru.mikheev.kirill.custombpm.repository.entity.ProcessStatus.FINISHED where p.id = ?1")
    void finishProcess(UUID processId);
}
