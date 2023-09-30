package ru.mikheev.kirill.custombpm.scheme.general.task;

import ru.mikheev.kirill.custombpm.service.TaskExecutor;

public class TimerTask extends TaskStage {

    private final long waitingTime;

    public TimerTask(String code, long waitingTime) {
        super(code);
        this.waitingTime = waitingTime;
    }

    @Override
    public void execute(TaskExecutor taskExecutor) {
        taskExecutor.executeTimerTask(this);
    }
}
