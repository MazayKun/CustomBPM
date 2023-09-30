package ru.mikheev.kirill.custombpm.scheme.general.task;

import ru.mikheev.kirill.custombpm.scheme.general.link.TaskLink;
import ru.mikheev.kirill.custombpm.service.TaskExecutor;

import java.util.List;

public class FinishTask extends TaskStage {

    public FinishTask(String code) {
        super(code);
    }

    @Override
    public void execute(TaskExecutor taskExecutor) {
        taskExecutor.executeFinishTask(this);
    }

    @Override
    public List<TaskLink> getOutgoingLinks() {
        throw new UnsupportedOperationException("No outgoing links for finish task");
    }

    @Override
    public TaskLink getDefaultLink() {
        throw new UnsupportedOperationException("No outgoing links for finish task");
    }
}
