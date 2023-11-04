package ru.mikheev.kirill.custombpm.scheme.general.task;

import ru.mikheev.kirill.custombpm.scheme.general.link.TaskLink;
import ru.mikheev.kirill.custombpm.service.TaskExecutor;

import java.util.UUID;

import static java.util.Objects.nonNull;

public abstract class TaskStage {

    protected final String code;
    protected TaskLink nextTaskLink;

    protected TaskStage(String code) {
        this.code = code;
    }

    public void addLink(TaskLink link) {
        if (nonNull(this.nextTaskLink)) {
            throw new RuntimeException("There should be only one outgoing link " + code);
        }
        this.nextTaskLink = link;
    }

    public String getCode() {
        return code;
    }

    public abstract void execute(UUID processId, String branchCode, TaskExecutor taskExecutor);
}
