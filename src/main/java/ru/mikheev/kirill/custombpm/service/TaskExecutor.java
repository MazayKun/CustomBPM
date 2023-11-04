package ru.mikheev.kirill.custombpm.service;

import ru.mikheev.kirill.custombpm.scheme.general.task.*;

import java.util.UUID;

public interface TaskExecutor {

    void executeCallTask(UUID processId, String branchCode, CallTask callTask);

    void executeCallWithResultTask(UUID processId, String branchCode, CallWithResultTask callWithResultTask);

    void executeTimerTask(UUID processId, String branchCode, TimerTask timerTask);

    void executeFinishTask(UUID processId, String branchCode, FinishTask finishTask);

    void executeGateTask(UUID processId, String branchCode, GateTask gateTask);
}
