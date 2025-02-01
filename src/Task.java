import java.util.Objects;

public class Task {
    private String name;
    private String description;
    private int id;
    private TaskStatus status;

    //конструктор для создания новых задач
    Task(String name, String description, TaskStatus status) {
        this.name = name;
        this.description = description;
        this.status = status;
        this.id = 0;
    }

    //конструктор для создания измененных задач
    Task(String name, String description, TaskStatus status, int id) {
        this.name = name;
        this.description = description;
        this.status = status;
        this.id = id;
    }

    //конструктор для создания нового эпика
    Task(String name, String description) {
        this.name = name;
        this.description = description;
        this.id = 0;
    }

    //конструктор для создания измененного эпика
    Task(String name, String description, int id) {
        this.name = name;
        this.description = description;
        this.id = id;
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
}
