import java.util.ArrayList;

public class Epic extends Task {

    private ArrayList<Subtask> subtasks;

    Epic(String name, String description, int id) {
        super(name, description, id);
        subtasks = new ArrayList<>();
    }

    public void addSubtaskToEpic(Subtask subtask){
        subtasks.add(subtask);
    }

    public ArrayList<Subtask> getSubtasks() {
        return subtasks;
    }

    public void changeSubtask(Subtask newSubtask){
        int index = 0;
        for(Subtask subtask : subtasks){
            if (subtask.equals(newSubtask)){
                subtasks.set(index, newSubtask);
                break;
            }
            index++;
        }
    }

    public void removeSubtask(Subtask subtask){
        subtasks.remove(subtask);
    }


    public void updateStatus(){
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
        if (newTaskCount == subtasks.size()){
            status = TaskStatus.NEW;
        } else if(doneTaskCount == subtasks.size()){
            status = TaskStatus.DONE;
        }
        setStatus(status);
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