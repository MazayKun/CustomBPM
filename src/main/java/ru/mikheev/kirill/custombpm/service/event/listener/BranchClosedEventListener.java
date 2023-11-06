package ru.mikheev.kirill.custombpm.service.event.listener;

import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import ru.mikheev.kirill.custombpm.repository.ExecutionBranchesRepository;
import ru.mikheev.kirill.custombpm.repository.ProcessesRepository;
import ru.mikheev.kirill.custombpm.repository.entity.ExecutionBranch;
import ru.mikheev.kirill.custombpm.service.event.dto.BranchClosedEvent;

import java.util.List;

@Component
@RequiredArgsConstructor
public class BranchClosedEventListener implements ApplicationListener<BranchClosedEvent> {

    private final ExecutionBranchesRepository executionBranchesRepository;
    private final ProcessesRepository processesRepository;

    @Override
    @Transactional
    public void onApplicationEvent(BranchClosedEvent event) {
        List<ExecutionBranch> allBranchesByProcess = executionBranchesRepository.findAllByProcessId(event.getProcessId());

        if (allBranchesByProcess.stream()
                .noneMatch(ExecutionBranch::isUnfinished)
        ) {
            if (allBranchesByProcess.stream()
                    .anyMatch(ExecutionBranch::isError)) {
                processesRepository.terminateProcessWithError(event.getProcessId());
            } else {
                processesRepository.finishProcess(event.getProcessId());
            }
        }
    }
}
