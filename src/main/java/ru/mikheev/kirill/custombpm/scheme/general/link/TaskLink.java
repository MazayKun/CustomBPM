package ru.mikheev.kirill.custombpm.scheme.general.link;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import ru.mikheev.kirill.custombpm.scheme.general.LogicalExpression;
import ru.mikheev.kirill.custombpm.scheme.general.task.TaskStage;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class TaskLink {
    @Getter
    private final TaskStage to;
    @Getter
    private final TransitionType transitionType;
    private LogicalExpression logicalExpression;

    public static TaskLink guaranteedLink(TaskStage to) {
        return new TaskLink(to, TransitionType.GUARANTEED);
    }

    public static TaskLink defaultLink(TaskStage to) {
        return new TaskLink(to, TransitionType.DEFAULT);
    }

    public static TaskLink conditionLink(TaskStage to, LogicalExpression logicalExpression) {
        var taskLink =  new TaskLink(to, TransitionType.EXPRESSION);
        taskLink.logicalExpression = logicalExpression;
        return taskLink;
    }
}
