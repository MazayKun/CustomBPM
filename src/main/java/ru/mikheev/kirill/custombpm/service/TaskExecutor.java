package ru.mikheev.kirill.custombpm.service;

import ru.mikheev.kirill.custombpm.scheme.general.task.*;

public interface TaskExecutor {

    void startNewProcess(StartTask startTask);

    void executeGateTask(GateTask gateTask);

    void executeCallTask(CallTask callTask);

    void executeCallWithResultTask(CallWithResultTask callWithResultTask);

    void executeTimerTask(TimerTask timerTask);

    void executeFinishTask(FinishTask finishTask);
}
