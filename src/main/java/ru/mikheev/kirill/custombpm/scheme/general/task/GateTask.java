package ru.mikheev.kirill.custombpm.scheme.general.task;

import ru.mikheev.kirill.custombpm.scheme.general.link.TaskLink;
import ru.mikheev.kirill.custombpm.scheme.general.link.TransitionType;
import ru.mikheev.kirill.custombpm.service.TaskExecutor;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static java.util.Objects.nonNull;

public class GateTask extends TaskStage {

    private final GateType type;
    private final List<TaskLink> outgoingLinks = new ArrayList<>();
    private TaskLink defaultLink;
    private int incomingLinksCounter;

    public GateTask(GateType type, String code) {
        super(code);
        this.type = type;
    }

    @Override
    public void addLink(TaskLink link) {
        if (link.getTransitionType() == TransitionType.DEFAULT) {
            if (nonNull(this.defaultLink))
                throw new RuntimeException("It must be only one default link for task with code " + code);
            this.defaultLink = link;
            return;
        }
        outgoingLinks.add(link);
    }

    public void setIncomingLinksCounter(int incomingLinksCounter) {
        this.incomingLinksCounter = incomingLinksCounter;
    }

    public int getIncomingLinksCounter() {
        return incomingLinksCounter;
    }

    @Override
    public void execute(UUID processId, String branchCode, TaskExecutor taskExecutor) {
        taskExecutor.executeGateTask(processId, branchCode, this);
    }
}
