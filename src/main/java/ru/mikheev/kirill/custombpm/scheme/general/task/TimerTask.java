package ru.mikheev.kirill.custombpm.scheme.general.task;

import ru.mikheev.kirill.custombpm.service.TaskExecutor;

import java.util.UUID;

public class TimerTask extends TaskStage {

    private final long waitingTime; // TODO заменить на cron нотацию

    public TimerTask(String code, long waitingTime) {
        super(code);
        this.waitingTime = waitingTime;
    }

    public long getWaitingTime() {
        return waitingTime;
    }

    @Override
    public void execute(UUID processId, String branchCode, TaskExecutor taskExecutor) {
        taskExecutor.executeTimerTask(processId, branchCode, this);
    }
}
