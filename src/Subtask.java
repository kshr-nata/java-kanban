import java.time.Duration;
import java.time.LocalDateTime;

public class Subtask extends Task {
    private final Epic epic;

    Subtask(String name, String description, TaskStatus status, Epic epic, LocalDateTime startTime, Duration duration) {
        super(name, description, status, startTime, duration);
        this.epic = epic;
    }

    Subtask(String name, String description, TaskStatus status, Epic epic, int id, LocalDateTime startTime, Duration duration) {
        super(name, description, status, id, startTime, duration);
        this.epic = epic;
    }

    @Override
    public TaskType getType() {
        return TaskType.SUBTASK;
    }

    public Epic getEpic() {
        return epic;
    }

    @Override
    public String toString() {
        return "Subtask{" +
                "name='" + getName() + '\'' +
                ", description='" + getDescription() + '\'' +
                ", id=" + getId() +
                ", status=" + getStatus() +
                ", epic=" + getEpic() +
                '}';
    }
}
