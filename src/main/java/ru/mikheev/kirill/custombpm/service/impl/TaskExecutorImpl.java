package ru.mikheev.kirill.custombpm.service.impl;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import ru.mikheev.kirill.custombpm.common.exception.CallEndpointException;
import ru.mikheev.kirill.custombpm.repository.BranchErrorsRepository;
import ru.mikheev.kirill.custombpm.repository.ExecutionBranchesRepository;
import ru.mikheev.kirill.custombpm.repository.ProcessVariablesRepository;
import ru.mikheev.kirill.custombpm.repository.ProcessesRepository;
import ru.mikheev.kirill.custombpm.repository.entity.ProcessVariable;
import ru.mikheev.kirill.custombpm.scheme.general.InputParameter;
import ru.mikheev.kirill.custombpm.scheme.general.task.*;
import ru.mikheev.kirill.custombpm.service.IntegrationService;
import ru.mikheev.kirill.custombpm.service.TaskExecutor;
import ru.mikheev.kirill.custombpm.service.dto.CallEndpointCommand;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ForkJoinPool;
import java.util.stream.Collectors;

import static java.util.Objects.isNull;

@Slf4j
@Service
@RequiredArgsConstructor
public class TaskExecutorImpl implements TaskExecutor {

    private final ExecutorService executionPool = ForkJoinPool.commonPool();

    private final ProcessesRepository processesRepository;
    private final ProcessVariablesRepository processVariablesRepository;
    private final ExecutionBranchesRepository executionBranchesRepository;
    private final BranchErrorsRepository branchErrorsRepository;

    private final IntegrationService integrationService;

    @Async("threadPoolTaskExecutor")
    @Transactional
    @Override
    public void executeCallTask(UUID processId, String branchCode, CallTask callTask) {
        Map<String, String> requiredParameterNamesMap = callTask.getInputParameters().stream()
                .collect(Collectors.toMap(InputParameter::getInnerName, InputParameter::getName));
        Map<String, String> parameters = processVariablesRepository.findAllByProcessIdAndNameIn(
                        processId,
                        requiredParameterNamesMap.keySet()
                ).stream()
                .collect(Collectors.toMap(
                        processVariable -> {
                            String requestName = requiredParameterNamesMap.get(processVariable.getName());
                            if (isNull(requestName)) throw new RuntimeException(String.format("Parameter with name %s for process %s not found", processVariable.getName(), processId));
                            return requestName;
                        },
                        ProcessVariable::getValue));
        try {
            integrationService.callEndpoint(
                    new CallEndpointCommand(
                            processId,
                            branchCode,
                            callTask.getEndpoint(),
                            parameters
                    )
            );
        } catch (CallEndpointException e) {

        }

    }

    @Async("threadPoolTaskExecutor")
    @Override
    public void executeCallWithResultTask(UUID processId, String branchCode, CallWithResultTask callWithResultTask) {

    }

    @Async("threadPoolTaskExecutor")
    @Override
    public void executeTimerTask(UUID processId, String branchCode, TimerTask timerTask) {

    }

    @Async("threadPoolTaskExecutor")
    @Override
    public void executeFinishTask(UUID processId, String branchCode, FinishTask finishTask) {

    }

    @Async("threadPoolTaskExecutor")
    @Override
    public void executeGateTask(UUID processId, String branchCode, GateTask gateTask) {

    }

    private void interruptBranchExecutionWithError(UUID processId, String branchCode, Exception e) {

    }
}
