import java.util.List;

public class CSVTaskFormat {

    public static String toString(Task task) {
        String taskString = task.getId() + "," + task.getType() + "," + task.getName() + "," + task.getStatus() + "," + task.getDescription();
        if (task.getType() == TaskType.SUBTASK) {
            taskString = taskString + "," + ((Subtask) task).getEpic().getId();
        }
        return taskString;
    }

    public static Task taskFromString(String value, TaskManager taskManager) throws ManagerLoadFromFileException {
        final String[] values = value.split(",");
        if (values.length < 5) {
            throw new ManagerLoadFromFileException("Ошибка при чтении строки задачи");
        }
        final int id = Integer.parseInt(values[0]);
        final TaskType type = TaskType.valueOf(values[1]);
        if (type == TaskType.TASK) {
            Task task = new Task(values[2], values[4], TaskStatus.valueOf(values[3]), id);
            return task;
        } else if (type == TaskType.EPIC) {
            Epic epic = new Epic(values[2], values[4], id);
            return epic;
        } else if (type == TaskType.SUBTASK) {
            if (values.length < 6) {
                throw new ManagerLoadFromFileException("Ошибка при чтении строки подзадачи");
            }
            final int epicId = Integer.parseInt(values[5]);
            Subtask subtask = new Subtask(values[2], values[4], TaskStatus.valueOf(values[3]), taskManager.getEpic(epicId), id);
            return subtask;
        }
        return null;
    }
}
