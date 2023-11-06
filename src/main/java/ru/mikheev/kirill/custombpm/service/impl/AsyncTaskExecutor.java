package ru.mikheev.kirill.custombpm.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;
import ru.mikheev.kirill.custombpm.repository.BranchExceptionsRepository;
import ru.mikheev.kirill.custombpm.repository.ExecutionBranchesRepository;
import ru.mikheev.kirill.custombpm.repository.ProcessVariablesRepository;
import ru.mikheev.kirill.custombpm.repository.ProcessesRepository;
import ru.mikheev.kirill.custombpm.repository.entity.ExecutionBranch;
import ru.mikheev.kirill.custombpm.repository.entity.ProcessVariable;
import ru.mikheev.kirill.custombpm.scheme.general.InputParameter;
import ru.mikheev.kirill.custombpm.scheme.general.link.TaskLink;
import ru.mikheev.kirill.custombpm.scheme.general.link.TransitionType;
import ru.mikheev.kirill.custombpm.scheme.general.task.*;
import ru.mikheev.kirill.custombpm.service.IntegrationService;
import ru.mikheev.kirill.custombpm.service.TaskExecutor;
import ru.mikheev.kirill.custombpm.service.dto.CallEndpointCommand;
import ru.mikheev.kirill.custombpm.service.event.EventPublisher;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.Executor;
import java.util.stream.Collectors;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;

@Slf4j
@Async("threadPoolTaskExecutor")
@Service
public class AsyncTaskExecutor implements TaskExecutor {

    private final ProcessesRepository processesRepository;
    private final ProcessVariablesRepository processVariablesRepository;
    private final ExecutionBranchesRepository executionBranchesRepository;
    private final BranchExceptionsRepository branchExceptionsRepository;

    private final IntegrationService integrationService;
    private final EventPublisher eventPublisher;

    private final TransactionTemplate transactionTemplate;
    private final Executor threadPool;

    public AsyncTaskExecutor(
            ProcessesRepository processesRepository,
            ProcessVariablesRepository processVariablesRepository,
            ExecutionBranchesRepository executionBranchesRepository,
            BranchExceptionsRepository branchExceptionsRepository,
            IntegrationService integrationService,
            EventPublisher eventPublisher,
            PlatformTransactionManager transactionManager,
            @Qualifier("threadPoolTaskExecutor") Executor threadPool) {
        this.processesRepository = processesRepository;
        this.processVariablesRepository = processVariablesRepository;
        this.executionBranchesRepository = executionBranchesRepository;
        this.branchExceptionsRepository = branchExceptionsRepository;
        this.integrationService = integrationService;
        this.eventPublisher = eventPublisher;
        this.transactionTemplate = new TransactionTemplate(transactionManager);
        this.threadPool = threadPool;
    }

    @Override
    public void executeCallTask(UUID processId, String branchCode, CallTask callTask) {
        transactionTemplate.executeWithoutResult(status -> {
            setBranchToCurrTask(processId, branchCode, callTask);
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
            integrationService.callEndpoint(
                    new CallEndpointCommand(
                            processId,
                            branchCode,
                            callTask.getEndpoint(),
                            parameters
                    )
            );
        });
        TaskLink nextTaskLink = callTask.getNextTaskLink();
        if (nextTaskLink.getTransitionType() == TransitionType.EXPRESSION) {
            List<ProcessVariable> processVariableList = processVariablesRepository.findAllByProcessId(processId);
            Map<String, Object> variablesValuesMap = processVariableList.stream()
                            .collect(Collectors.toMap(ProcessVariable::getName, ProcessVariable::getObjectRepresentation)); // TODO :  Корректно обрабатывать ошибку
            if (!nextTaskLink.checkPredicateOperation(variablesValuesMap)) {
                log.warn("Condition for link from call task with code {} to next task is false for process {}", callTask.getCode(), processId);
                return;
            }
        }
        nextTaskLink.getTo().execute(
                processId,
                branchCode,
                this
        );
    }

    @Override
    public void executeCallWithResultTask(UUID processId, String branchCode, CallWithResultTask callWithResultTask) {
        transactionTemplate.executeWithoutResult(status -> {
            setBranchToCurrTask(processId, branchCode, callWithResultTask);
            Map<String, String> requiredParameterNamesMap = callWithResultTask.getInputParameters().stream()
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
            integrationService.callEndpoint(
                    new CallEndpointCommand(
                            processId,
                            branchCode,
                            callWithResultTask.getEndpoint(),
                            parameters
                    )
            );
        });
    }

    @Override
    public void executeTimerTask(UUID processId, String branchCode, TimerTask timerTask) {
        transactionTemplate.executeWithoutResult(status -> {
            setBranchToCurrTask(processId, branchCode, timerTask);
            try {
                // TODO использовать таблицу в бд, в которой будет храниться время, когда процесс должен продолжить работу и шедулером обновлять атймеры в коде
                Thread.sleep(timerTask.getWaitingTime());
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });
    }

    @Override
    public void executeFinishTask(UUID processId, String branchCode, FinishTask finishTask) {
        transactionTemplate.executeWithoutResult(status -> executionBranchesRepository.finishBranch(branchCode, processId, finishTask.getCode()));
        eventPublisher.publishBranchClosedEvent(branchCode, processId);
    }

    @Override
    public void executeGateTask(UUID processId, String branchCode, GateTask gateTask) {
        switch (gateTask.getType()) {
            case SINGLETON -> executeSingleton(processId, branchCode, gateTask);
            case PARALLEL -> executeParallel(processId, branchCode, gateTask);
            default -> throw new RuntimeException();
        }
    }

    private void executeSingleton(UUID processId, String branchCode, GateTask gateTask) {
        Optional<ExecutionBranch> newBranchOpt = transactionTemplate.execute(status -> {
            executionBranchesRepository.gatePassed(branchCode, processId, gateTask.getCode());
            List<ExecutionBranch> branchesPassedGate = executionBranchesRepository.findAllBranchesThatPassedGate(processId, gateTask.getCode());
            if (branchesPassedGate.size() < gateTask.getIncomingLinksCounter()) {
                return Optional.empty();
            }
            ExecutionBranch currBranch = executionBranchesRepository.findByCodeAndProcessId(branchCode, processId)
                            .orElseThrow(() -> new RuntimeException(String.format("Branch %s for process %s not found", branchCode, processId)));
            return Optional.of(executionBranchesRepository.save(ExecutionBranch.newBranch(currBranch, gateTask.getCode())));
        });
        if (newBranchOpt.isEmpty()) return;
        ExecutionBranch newBranch = newBranchOpt.get();
        Map<String, Object> processVariables = processVariablesRepository.findAllByProcessId(processId).stream()
                .collect(Collectors.toMap(ProcessVariable::getName, ProcessVariable::getObjectRepresentation));
        for (TaskLink outgoingLink : gateTask.getOutgoingLinks()) {
            switch (outgoingLink.getTransitionType()) {
                case GUARANTEED -> {
                    log.warn("Singleton gate is best used with conditions");
                    outgoingLink.getTo().execute(processId, newBranch.getCode(), this);
                    return;
                }
                case DEFAULT -> throw new RuntimeException("There should be only one default link in gate");
                case EXPRESSION -> {
                    if (outgoingLink.checkPredicateOperation(processVariables)) {
                        outgoingLink.getTo().execute(processId, newBranch.getCode(), this);
                        return;
                    }
                }
                default -> throw new RuntimeException("Unknown link transition type " + outgoingLink.getTransitionType());
            }
        }
        if (isNull(gateTask.getDefaultLink())) {
            throw new RuntimeException(String.format("All links for singleton gate %s unreachable, but default link not found", gateTask.getCode()));
        }
        gateTask.getDefaultLink().getTo().execute(processId, newBranch.getCode(), this);
    }

    private void executeParallel(UUID processId, String branchCode, GateTask gateTask) {
        ExecutionBranch currBranch = transactionTemplate.execute(status -> {
            executionBranchesRepository.gatePassed(branchCode, processId, gateTask.getCode());
            return executionBranchesRepository.findByCodeAndProcessId(branchCode, processId)
                    .orElseThrow(() -> new RuntimeException(String.format("Branch %s for process %s not found", branchCode, processId)));
        });
        Map<String, Object> processVariables = processVariablesRepository.findAllByProcessId(processId).stream()
                .collect(Collectors.toMap(ProcessVariable::getName, ProcessVariable::getObjectRepresentation));
        int branchCounter = 1;
        for (TaskLink outgoingLink : gateTask.getOutgoingLinks()) {
            switch (outgoingLink.getTransitionType()) {
                case DEFAULT : throw new RuntimeException("There should be only one default link in gate");
                case EXPRESSION : {
                    if (!outgoingLink.checkPredicateOperation(processVariables)) {
                        break;
                    }
                }
                case GUARANTEED : {
                    final int newOrder = branchCounter;
                    branchCounter++;
                    threadPool.execute(() -> {
                        ExecutionBranch newBranch = executionBranchesRepository.save(
                                ExecutionBranch.newBranchWithCounter(currBranch, gateTask.getCode(), newOrder)
                        );
                        outgoingLink.getTo().execute(processId, newBranch.getCode(), this);
                    });
                    break;
                }
                default: throw new RuntimeException("Unknown link transition type " + outgoingLink.getTransitionType());
            }
        }
        if (nonNull(gateTask.getDefaultLink())) {
            log.warn("Default links not recommended for parallel gate " + gateTask.getCode());
            final int newOrder = branchCounter;
            threadPool.execute(() -> {
                ExecutionBranch newBranch = executionBranchesRepository.save(
                        ExecutionBranch.newBranchWithCounter(currBranch, gateTask.getCode(), newOrder)
                );
                gateTask.getDefaultLink().getTo().execute(processId, newBranch.getCode(), this);
            });
        }
    }

    private void setBranchToCurrTask(UUID processId, String branchCode, TaskStage task) {
        executionBranchesRepository.nextTask(branchCode, processId, task.getCode());
    }
}
