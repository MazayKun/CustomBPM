package ru.mikheev.kirill.custombpm.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.mikheev.kirill.custombpm.repository.entity.Process;

import java.util.UUID;

@Repository
public interface ProcessesRepository extends JpaRepository<Process, UUID> {
}
