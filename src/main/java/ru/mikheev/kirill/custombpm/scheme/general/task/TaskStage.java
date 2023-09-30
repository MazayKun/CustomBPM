package ru.mikheev.kirill.custombpm.scheme.general.task;

import ru.mikheev.kirill.custombpm.scheme.general.link.TaskLink;
import ru.mikheev.kirill.custombpm.scheme.general.link.TransitionType;
import ru.mikheev.kirill.custombpm.service.TaskExecutor;

import java.util.ArrayList;
import java.util.List;

import static java.util.Objects.nonNull;

public abstract class TaskStage {

    private final String code;
    private List<TaskLink> outgoingLinks = new ArrayList<>();
    private TaskLink defaultLink;

    protected TaskStage(String code) {
        this.code = code;
    }

    public void addLink(TaskLink outgoingLink) {
        if(outgoingLink.getTransitionType() == TransitionType.DEFAULT) {
            if(nonNull(this.defaultLink)) throw new RuntimeException("It must be only one default link for task with code " + code);
            this.defaultLink = outgoingLink;
            return;
        }
        outgoingLinks.add(outgoingLink);
    }

    public String getCode() {
        return code;
    }

    public List<TaskLink> getOutgoingLinks() {
        return outgoingLinks;
    }

    public TaskLink getDefaultLink() {
        return defaultLink;
    }

    public abstract void execute(TaskExecutor taskExecutor);
}
