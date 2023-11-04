package ru.mikheev.kirill.custombpm.scheme.general.link;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import ru.mikheev.kirill.custombpm.scheme.condition.operation.PredicateOperation;
import ru.mikheev.kirill.custombpm.scheme.general.task.TaskStage;

import java.util.Map;

import static java.util.Objects.isNull;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class TaskLink {
    @Getter
    private final TaskStage to;
    @Getter
    private final TransitionType transitionType;
    private PredicateOperation predicateOperation;

    public PredicateOperation getPredicateOperation() {
        return predicateOperation;
    }

    public boolean checkPredicateOperation(Map<String, Object> variables) {
        if (isNull(predicateOperation)) {
            throw new RuntimeException("Attempt to check predicate operation for link with transition type " + transitionType);
        }
        return predicateOperation.getResultByData(variables);
    }

    public static TaskLink guaranteedLink(TaskStage to) {
        return new TaskLink(to, TransitionType.GUARANTEED);
    }

    public static TaskLink defaultLink(TaskStage to) {
        return new TaskLink(to, TransitionType.DEFAULT);
    }

    public static TaskLink conditionLink(TaskStage to, PredicateOperation predicateOperation) {
        var taskLink = new TaskLink(to, TransitionType.EXPRESSION);
        taskLink.predicateOperation = predicateOperation;
        return taskLink;
    }
}
