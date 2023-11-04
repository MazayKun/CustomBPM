package ru.mikheev.kirill.custombpm.scheme.general;

import ru.mikheev.kirill.custombpm.common.DataType;
import ru.mikheev.kirill.custombpm.scheme.general.task.StartTask;
import ru.mikheev.kirill.custombpm.scheme.general.task.TaskStage;

import java.util.*;

public class Scheme {

    private String name;
    private StartTask startTask;
    private Map<String, TaskStage> allTasksByCode = new HashMap<>();
    private Map<String, DataType> parametersTypes;

    public Scheme(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public List<InitParameter> getInitParameters() {
        return startTask.getInitParameters();
    }

    public String getStartPointCode() {
        return startTask.getCode();
    }

    public void addStartTask(StartTask startTask) {
        this.startTask = startTask;
        allTasksByCode.put(startTask.getCode(), startTask);
    }

    public StartTask getStartTask() {
        return startTask;
    }

    public void setParametersTypes(Map<String, DataType> parametersTypes) {
        this.parametersTypes = parametersTypes;
    }

    public Map<String, DataType> getParametersTypes() {
        return parametersTypes;
    }

    public void addTask(TaskStage task) {
        allTasksByCode.put(task.getCode(), task);
    }

    public Optional<TaskStage> getTaskStageByCode(String stageCode) {
        return Optional.ofNullable(allTasksByCode.get(stageCode));
    }

    public List<TaskStage> getAllTasks() {
        List<TaskStage> allTasksCopy = new ArrayList<>(allTasksByCode.values());
        allTasksCopy.add(startTask);
        return allTasksCopy;
    }
}
