package ru.mikheev.kirill.custombpm.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.mikheev.kirill.custombpm.common.DataType;
import ru.mikheev.kirill.custombpm.repository.ExecutionBranchesRepository;
import ru.mikheev.kirill.custombpm.repository.ProcessVariablesRepository;
import ru.mikheev.kirill.custombpm.repository.ProcessesRepository;
import ru.mikheev.kirill.custombpm.repository.entity.ExecutionBranch;
import ru.mikheev.kirill.custombpm.repository.entity.Process;
import ru.mikheev.kirill.custombpm.repository.entity.ProcessVariable;
import ru.mikheev.kirill.custombpm.scheme.SchemeStorage;
import ru.mikheev.kirill.custombpm.scheme.general.Scheme;
import ru.mikheev.kirill.custombpm.scheme.general.link.TaskLink;
import ru.mikheev.kirill.custombpm.scheme.general.link.TransitionType;
import ru.mikheev.kirill.custombpm.scheme.general.task.CallWithResultTask;
import ru.mikheev.kirill.custombpm.scheme.general.task.TaskStage;
import ru.mikheev.kirill.custombpm.service.ProcessLifecycleService;
import ru.mikheev.kirill.custombpm.service.TaskExecutor;
import ru.mikheev.kirill.custombpm.service.dto.StartProcessDto;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProcessLifecycleServiceImpl implements ProcessLifecycleService {

    private final ProcessesRepository processesRepository;
    private final ProcessVariablesRepository processVariablesRepository;
    private final ExecutionBranchesRepository executionBranchesRepository;

    private final ProcessService processService;
    private final SchemeStorage schemeStorage;
    private final TaskExecutor taskExecutor;

    @Override
    public void startNewProcess(String schemeName, Map<String, String> parameters) {
        Scheme scheme = schemeStorage.getSchemeByName(schemeName)
                .orElseThrow(() -> new RuntimeException(String.format("Scheme with name %s not found", schemeName)));
        StartProcessDto startProcessDto = processService.initNewProcess(scheme, parameters);
        TaskLink nextTaskLink = scheme.getStartTask().getNextTaskLink();
        if (nextTaskLink.getTransitionType() == TransitionType.EXPRESSION && !nextTaskLink.checkPredicateOperation(startProcessDto.getProcessVariables())) {
            log.warn("Condition for link from start to first task is false for process " + startProcessDto.getProcessId());
            return;
        }
        nextTaskLink.getTo().execute(
                startProcessDto.getProcessId(),
                startProcessDto.getBranchCode(),
                taskExecutor
        );
    }

    @Override
    public void processCallback(UUID processId, String branchCode, Map<String, String> parameters) {
        Process process = processesRepository.findById(processId)
                .orElseThrow(() -> new RuntimeException(String.format("Process %s not found", processId)));
        Scheme scheme = schemeStorage.getSchemeByName(process.getType())
                .orElseThrow(() -> new RuntimeException(String.format("Scheme with name %s not found for process %s", process.getType(), process.getId())));
        ExecutionBranch executionBranch = executionBranchesRepository.findByCodeAndProcessId(branchCode, processId)
                .orElseThrow(() -> new RuntimeException(String.format("Branch %s for process %s not found", branchCode, processId)));
        TaskStage currTaskStage = scheme.getTaskStageByCode(executionBranch.getCurrentBlockCode())
                .orElseThrow(() -> new RuntimeException(String.format("Task with code %s not found for process %s branch %s",
                        executionBranch.getCurrentBlockCode(),
                        executionBranch.getCode(),
                        processId
                )));
        if (!(currTaskStage instanceof CallWithResultTask)) {
            throw new RuntimeException(String.format("Only call with result tasks required callback, but %s found", currTaskStage.getClass()));
        }
        List<ProcessVariable> newProcessVariables = ((CallWithResultTask) currTaskStage).getOutputParameters().stream()
                .map(outputParameter -> {
                    String parameterValue = parameters.get(outputParameter.getName());
                    validateParameterStringRepresentation(outputParameter.getType(), parameterValue);
                    return new ProcessVariable(processId, outputParameter.getInnerName(), outputParameter.getType(), parameterValue);
                }).collect(Collectors.toList());
        processVariablesRepository.saveAll(newProcessVariables);
        ((CallWithResultTask) currTaskStage).getNextTaskLink().getTo().execute(processId, branchCode, taskExecutor);
    }

    private void validateParameterStringRepresentation(DataType type, String parameterRepresentation) {
        try {
            type.valueFromString(parameterRepresentation);
        } catch (Exception e) {
            throw new RuntimeException(String.format("Bad string parameter representation for type %s with value %s", type.name(), parameterRepresentation), e);
        }
    }
}
