package ru.mikheev.kirill.custombpm.scheme.general;

import ru.mikheev.kirill.custombpm.scheme.general.task.StartTask;
import ru.mikheev.kirill.custombpm.scheme.general.task.TaskStage;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class Scheme {

    private StartTask startTask;
    private Map<String, TaskStage> allTasksByCode = new HashMap<>();

    public void addStartTask(StartTask startTask) {
        this.startTask = startTask;
        allTasksByCode.put(startTask.getCode(), startTask);
    }

    public void addTask(TaskStage task) {
        allTasksByCode.put(task.getCode(), task);
    }

    public Optional<TaskStage> getTaskStageByCode(String stageCode) {
        return Optional.ofNullable(allTasksByCode.get(stageCode));
    }
}
