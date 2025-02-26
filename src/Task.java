import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Objects;

public class Task implements Comparable<Task> {
    private String name;
    private String description;
    private int id;
    private TaskStatus status;
    private Duration duration = Duration.ofMinutes(0);
    protected LocalDateTime startTime;

    //конструктор для создания новых задач
    Task(String name, String description, TaskStatus status, LocalDateTime startTime, Duration duration) {
        this.name = name;
        this.description = description;
        this.status = status;
        this.id = 0;
        this.duration = duration;
        this.startTime = startTime;
    }

    //конструктор для создания измененных задач
    Task(String name, String description, TaskStatus status, int id, LocalDateTime startTime, Duration duration) {
        this.name = name;
        this.description = description;
        this.status = status;
        this.id = id;
        this.duration = duration;
        this.startTime = startTime;
    }

    //конструктор для создания нового эпика
    Task(String name, String description) {
        this.name = name;
        this.description = description;
        this.id = 0;
        this.duration = Duration.ofMinutes(0);
    }

    //конструктор для создания измененного эпика
    Task(String name, String description, int id) {
        this.name = name;
        this.description = description;
        this.id = id;
        this.duration = Duration.ofMinutes(0);
    }

    public void setId(int id) {
        if (this.id == 0) {
            this.id = id;
        }
    }

    public int getId() {
        return id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public TaskStatus getStatus() {
        return status;
    }

    public void setStatus(TaskStatus status) {
        this.status = status;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public TaskType getType() {
        return TaskType.TASK;
    }

    public LocalDateTime getEndTime() {
        if (startTime == null) {
            return null;
        }
        return startTime.plusMinutes(duration.toMinutes());
    }

    public Duration getDuration() {
        return duration;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public void setDuration(Duration duration) {
        this.duration = duration;
    }

    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Task task = (Task) o;
        return id == task.id;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    @Override
    public String toString() {
        return "Task{" +
                "name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", id=" + id +
                ", status=" + status +
                '}';
    }

    @Override
    public int compareTo(Task o) {
        return this.startTime.compareTo(o.startTime);
    }
}
