import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;

public class FileBackedTaskManager extends  InMemoryTaskManager {
    private final File file;

    public FileBackedTaskManager(File file) {
        this.file = file;
    }

    @Override
    public void clearTasks() {
        super.clearTasks();
        save();
    }

    @Override
    public void clearEpics() {
        super.clearEpics();
        save();
    }

    @Override
    public void clearSubtasks() {
        super.clearSubtasks();
        save();
    }

    @Override
    public Task getTask(Integer id) {
        final Task task = super.getTask(id);
        save();
        return task;
    }

    @Override
    public Subtask getSubtask(Integer id) {
        final Subtask subtask = super.getSubtask(id);
        save();
        return subtask;
    }

    @Override
    public Epic getEpic(Integer id) {
        final Epic epic = super.getEpic(id);
        save();
        return epic;
    }

    //создание новых задач
    @Override
    public int makeNewTask(Task task) {
        final int id = super.makeNewTask(task);
        save();
        return id;
    }

    @Override
    public int makeNewEpic(Epic task) {
        final int id = super.makeNewEpic(task);
        save();
        return id;
    }

    @Override
    public int makeNewSubtask(Subtask task) {
        final int id = super.makeNewSubtask(task);
        save();
        return id;
    }

    //обновление задач
    @Override
    public void updateTask(Task newTask) {
        super.updateTask(newTask);
        save();
    }

    @Override
    public void updateSubtask(Subtask newTask) {
        super.updateSubtask(newTask);
        save();
    }

    @Override
    public void updateEpic(Epic newEpic) {
        super.updateEpic(newEpic);
        save();
    }

    //удаление задачи по айди
    @Override
    public void removeTask(int id) {
        super.removeTask(id);
        save();
    }

    @Override
    public void removeSubtask(int id) {
        super.removeSubtask(id);
        save();
    }

    @Override
    public void removeEpic(int id) {
        super.removeEpic(id);
        save();
    }

    public static FileBackedTaskManager loadFromFile(File file) throws ManagerLoadFromFileException {
        final FileBackedTaskManager taskManager = new FileBackedTaskManager(file);
        try {
            String contents = Files.readString(file.toPath());
            String[] blocks = contents.split("\n\n");
            if (blocks.length == 2) {
                String[] valuesOfTasks = blocks[0].split("\n");
                String[] valuesOfHistory = blocks[1].split(",");
                loadTasks(taskManager, valuesOfTasks);
                loadHistory(taskManager, valuesOfHistory);
            }
        } catch (IOException ex) {
            throw new ManagerLoadFromFileException("Ошибка чтения из файла", ex);
        }

        return taskManager;
    }

    protected void save() throws ManagerSaveException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
            writer.write("id,type,name,status,description,epic\n");
            for (Task task : getAllTasks()) {
                writer.write(toString(task) + "\n");
            }
            for (Epic epic : getAllEpics()) {
                writer.write(toString(epic) + "\n");
            }
            for (Subtask subtask : getAllSubtasks()) {
                writer.write(toString(subtask) + "\n");
            }
            writer.write("\n");
            for (Task task : getHistory()) {
                writer.write(task.getId() + ",");
            }
        } catch (IOException e) {
            throw new ManagerSaveException("Ошибка при сохранении данных", e);
        }
    }

    private void addTaskToMap(Task task) {
        TaskType type = task.getType();
        if (type == TaskType.TASK) {
            tasks.put(task.getId(), task);
        } else if (type == TaskType.EPIC) {
            epics.put(task.getId(), (Epic) task);
        } else if (type == TaskType.SUBTASK) {
            subtasks.put(task.getId(), (Subtask) task);
            Epic epic = ((Subtask) task).getEpic();
            epic.addSubtaskToEpic((Subtask) task);
            epic.updateStatus();
        }
    }

    private static void loadTasks(FileBackedTaskManager taskManager, String[] valuesOfTasks) {
        boolean isHeader = true;
        for (String taskString : valuesOfTasks) {
            if (!isHeader) {
                Task task = taskFromString(taskString, taskManager);
                if (task != null) {
                    taskManager.addTaskToMap(task);
                    taskManager.currentId = Integer.max(task.getId(), taskManager.currentId);
                }
            }
            isHeader = false;
        }
    }

    private static void loadHistory(FileBackedTaskManager taskManager, String[] valuesOfHistory) {
        //очищаем историю
        for (Task task : taskManager.getHistory()) {
            taskManager.historyManager.remove(task.getId());
        }
        for (String value : valuesOfHistory) {
            final int id = Integer.parseInt(value);
            if (taskManager.tasks.containsKey(id)) {
                taskManager.historyManager.add(taskManager.getTask(id));
            } else if (taskManager.epics.containsKey(id)) {
                taskManager.historyManager.add(taskManager.getEpic(id));
            } else if (taskManager.subtasks.containsKey(id)) {
                taskManager.historyManager.add(taskManager.getSubtask(id));
            }
        }
    }

    private static String toString(Task task) {
        String taskString = task.getId() + "," + task.getType() + "," + task.getName() + "," + task.getStatus() + "," + task.getDescription();
        if (task.getType() == TaskType.SUBTASK) {
            taskString = taskString + "," + ((Subtask) task).getEpic().getId();
        }
        return taskString;
    }

    private static Task taskFromString(String value, TaskManager taskManager) throws ManagerLoadFromFileException {
        final String[] values = value.split(",");
        if (values.length < 5) {
            throw new ManagerLoadFromFileException("Ошибка при чтении строки задачи");
        }
        final int id = Integer.parseInt(values[0]);
        final TaskType type = TaskType.valueOf(values[1]);
        if (type == TaskType.TASK) {
            return new Task(values[2], values[4], TaskStatus.valueOf(values[3]), id);
        } else if (type == TaskType.EPIC) {
            return new Epic(values[2], values[4], id);
        } else if (type == TaskType.SUBTASK) {
            if (values.length < 6) {
                throw new ManagerLoadFromFileException("Ошибка при чтении строки подзадачи");
            }
            final int epicId = Integer.parseInt(values[5]);
            return new Subtask(values[2], values[4], TaskStatus.valueOf(values[3]), taskManager.getEpic(epicId), id);
        }
        return null;
    }

}

