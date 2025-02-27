import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;

public class Epic extends Task {

    private final ArrayList<Subtask> subtasks;
    private LocalDateTime endTime = LocalDateTime.of(1,1,1,1,1);

    Epic(String name, String description) {
        super(name, description);
        subtasks = new ArrayList<>();
    }

    Epic(String name, String description, int id) {
        super(name, description, id);
        subtasks = new ArrayList<>();
    }

    @Override
    public void setStatus(TaskStatus status) {

    }

    @Override
    public TaskType getType() {
        return TaskType.EPIC;
    }

    public void addSubtaskToEpic(Subtask subtask) {
        subtasks.add(subtask);
        updateStatus();
        changeTerms();
    }

    public ArrayList<Subtask> getSubtasks() {
        return subtasks;
    }

    public void removeSubtask(Subtask subtask) {
        subtasks.remove(subtask);
        updateStatus();
        changeTerms();
    }

    public void clearSubtasks() {
        subtasks.clear();
        updateStatus();
        changeTerms();
    }

    public void changeTerms() {
        setStartTime(null);
        endTime = null;
        setDuration(Duration.ofMinutes(0));
        for (Subtask subtask : subtasks) {
            if (startTime == null) {
                setStartTime(subtask.getStartTime());
            } else if (startTime.isAfter(subtask.getStartTime())) {
                setStartTime(subtask.getStartTime());
            }
            if (endTime == null) {
                endTime = subtask.getEndTime();
            } else if (endTime.isBefore(subtask.getEndTime())) {
                endTime = subtask.getEndTime();
            }
        }
        if (startTime != null & endTime != null) {
            setDuration(Duration.between(startTime, endTime));
        }
    }

    public void updateStatus() {
        TaskStatus status = TaskStatus.IN_PROGRESS;
        int newTaskCount = 0;
        int doneTaskCount = 0;
        for (Subtask subtask : subtasks) {
            if (subtask.getStatus() == TaskStatus.NEW) {
                newTaskCount += 1;
            } else if (subtask.getStatus() == TaskStatus.DONE) {
                doneTaskCount += 1;
            }
        }
        if (newTaskCount == subtasks.size()) {
            status = TaskStatus.NEW;
        } else if (doneTaskCount == subtasks.size()) {
            status = TaskStatus.DONE;
        }
        super.setStatus(status);
    }

    @Override
    public LocalDateTime getEndTime() {
        return endTime;
    }

    @Override
    public String toString() {
        String result = "Epic{" +
                "name='" + getName() + '\'' +
                ", description='" + getDescription() + '\'' +
                ", id=" + getId() +
                ", status=" + getStatus() +
                ", subtasks.size()=" + subtasks.size();
        result = result + '}';
        return result;
    }
}
