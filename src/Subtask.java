public class Subtask extends Task {
    private final Epic epic;

    Subtask(String name, String description, TaskStatus status, Epic epic) {
        super(name, description, status);
        this.epic = epic;
    }

    Subtask(String name, String description, TaskStatus status, Epic epic, int id) {
        super(name, description, status, id);
        this.epic = epic;
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
