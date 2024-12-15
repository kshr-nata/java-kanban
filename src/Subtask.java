public class Subtask extends Task {
    private Epic epic;

    Subtask(String name, String description, int id, Epic epic) {
        super(name, description, id);
        this.epic = epic;
    }

    public Epic getEpic() {
        return epic;
    }

    public void setEpic(Epic epic) {
        this.epic = epic;
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
