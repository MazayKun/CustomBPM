package ru.mikheev.kirill.custombpm.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import ru.mikheev.kirill.custombpm.common.DataType;
import ru.mikheev.kirill.custombpm.repository.BranchExceptionsRepository;
import ru.mikheev.kirill.custombpm.repository.ExecutionBranchesRepository;
import ru.mikheev.kirill.custombpm.repository.ProcessVariablesRepository;
import ru.mikheev.kirill.custombpm.repository.ProcessesRepository;
import ru.mikheev.kirill.custombpm.repository.entity.Process;
import ru.mikheev.kirill.custombpm.repository.entity.*;
import ru.mikheev.kirill.custombpm.scheme.general.InitParameter;
import ru.mikheev.kirill.custombpm.scheme.general.Scheme;
import ru.mikheev.kirill.custombpm.service.dto.StartProcessDto;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

import static java.util.Objects.isNull;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProcessService { // TODO : Объединить с ProcessLifecycleServiceImpl

    private final ProcessesRepository processesRepository;
    private final ProcessVariablesRepository processVariablesRepository;
    private final ExecutionBranchesRepository executionBranchesRepository;
    private final BranchExceptionsRepository branchExceptionsRepository;

    //{
    //    "condition" : true,
    //    "test_value" : 123
    //}

    @Transactional
    public StartProcessDto initNewProcess(Scheme scheme, Map<String, String> parameters) {
        Process newProcess = processesRepository.save(Process.newProcess(scheme.getName()));
        ExecutionBranch executionBranch = executionBranchesRepository.save(
                ExecutionBranch.rootBranch(newProcess.getId(), scheme.getStartPointCode())
        );
        List<InitParameter> initParameters = scheme.getInitParameters();
        List<ProcessVariable> initVariables = new ArrayList<>(initParameters.size());
        String parameterRepresentation;
        for (InitParameter initParameter : initParameters) {
            parameterRepresentation = parameters.get(initParameter.getName());
            if (isNull(parameterRepresentation)) {
                throw new RuntimeException(String.format("Required parameter %s not found", initParameter.getName()));
            }
            validateParameterStringRepresentation(initParameter.getType(), parameterRepresentation);
            initVariables.add(
                    new ProcessVariable(
                            newProcess.getId(),
                            initParameter.getName(),
                            initParameter.getType(),
                            parameterRepresentation
                    )
            );
        }
        return new StartProcessDto(
                newProcess.getId(),
                executionBranch.getCode(),
                mapVariablesToCorrespondingType(processVariablesRepository.saveAll(initVariables))
        );
    }

    @Transactional(isolation = Isolation.READ_COMMITTED)
    public void interruptBranchExecutionWithError(UUID processId, String branchCode, Throwable cause) {
        ExecutionBranch executionBranch = executionBranchesRepository.findByCodeAndProcessId(branchCode, processId)
                .orElseThrow(() -> new RuntimeException(String.format("Branch %s for process %s not found", branchCode, processId)));
        executionBranch.setStatus(BranchStatus.ERROR);
        executionBranchesRepository.save(executionBranch);
        branchExceptionsRepository.save(new BranchException(executionBranch.getId(), cause.getMessage(), ExceptionUtils.getStackTrace(cause)));
    }

    private Map<String, Object> mapVariablesToCorrespondingType(List<ProcessVariable> variables) {
        return variables.stream()
                .collect(Collectors.toMap(ProcessVariable::getName, ProcessVariable::getObjectRepresentation)); // TODO: Корректно обрабатывать ошибку
    }

    private void validateParameterStringRepresentation(DataType type, String parameterRepresentation) {
        try {
            type.valueFromString(parameterRepresentation);
        } catch (Exception e) {
            throw new RuntimeException(String.format("Bad string parameter representation for type %s with value %s", type.name(), parameterRepresentation), e);
        }
    }
}
