import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class SubtaskTest {

    TaskManager taskManager;

    @BeforeEach
    public void beforeEach(){
        taskManager = Managers.getDefault();
    }

    @Test
    void addNewSubtask() {
        Epic epic = new Epic("Test addNewSubtask", "Test addNewSubtask description");
        final int epicId = taskManager.makeNewEpic(epic);

        Subtask subtask = new Subtask("Test addNewSubtask", "Test addNewSubtask description", TaskStatus.NEW, epic);
        final int subtaskId = taskManager.makeNewSubtask(subtask);

        final Subtask savedSubtask = taskManager.getSubtask(subtaskId);

        assertNotNull(savedSubtask, "Сабтаск не найден.");
        assertEquals(subtask, savedSubtask, "Сабтаски не совпадают.");

        final List<Subtask> subtasks = taskManager.getAllSubtasks();

        assertNotNull(subtasks, "Сабтаски не возвращаются.");
        assertEquals(1, subtasks.size(), "Неверное количество сабтасков.");
        assertEquals(subtask, subtasks.get(0), "Сабтаски не совпадают.");
        assertEquals(1, epic.getSubtasks().size(), "Неверное количество сабтасков у эпика.");
        assertEquals("Test addNewSubtask", subtask.getName(), "Имя сабтаска изменилось.");
        assertEquals("Test addNewSubtask description", subtask.getDescription(), "Описание сабтаска изменилось.");
        assertEquals(TaskStatus.NEW, subtask.getStatus(), "Статус сабтаска изменился.");
        assertEquals(epic, subtask.getEpic(), "Эпик сабтаска изменился");
    }

    @Test
    void shouldRemoveSubtask(){
        Epic epic = new Epic("Test addNewEpic", "Test addNewEpic description");
        final int epicId = taskManager.makeNewEpic(epic);
        Subtask subtask = new Subtask("Test addNewSubtask", "Test addNewSubtask description", TaskStatus.NEW, epic);
        final int subtaskId = taskManager.makeNewSubtask(subtask);
        taskManager.removeSubtask(subtaskId);
        assertEquals(0, taskManager.getAllSubtasks().size(),"Сабтаск не удаляется");

        ArrayList<Subtask> subtasks = epic.getSubtasks();
        Subtask foundSubtask = null;
        for (Subtask currentSubtask : subtasks){
            if(currentSubtask.equals(subtask)){
                foundSubtask = currentSubtask;
            }
        }
        assertEquals(null, foundSubtask, "Сабтаск не удаляется из подзадач эпика" );
    }

    @Test
    void shouldClearSubtasks(){
        Epic epic = new Epic("Test addNewEpic", "Test addNewEpic description");
        final int epicId = taskManager.makeNewEpic(epic);
        Subtask subtask = new Subtask("Test addNewSubtask", "Test addNewSubtask description", TaskStatus.NEW, epic);
        final int subtaskId = taskManager.makeNewSubtask(subtask);
        taskManager.clearSubtasks();
        assertEquals(0, taskManager.getAllSubtasks().size(),"Сабтаски не очищаются");
    }

    @Test
    void updateSubtask(){
        Epic epic = new Epic("Test", "Test description");
        final int epicId = taskManager.makeNewEpic(epic);
        Subtask subtask = new Subtask("Test", "Test description", TaskStatus.NEW, epic);
        final int subtaskId = taskManager.makeNewSubtask(subtask);
        Subtask secondSubtask = new Subtask("Test updateSubtask", "Test updateSubtask description", TaskStatus.IN_PROGRESS, epic, subtaskId);
        taskManager.updateSubtask(secondSubtask);
        assertEquals("Test updateSubtask", subtask.getName(), "Имя сабтаска не изменилось.");
        assertEquals("Test updateSubtask description", subtask.getDescription(), "Описание сабтаска не изменилось.");
        assertEquals(TaskStatus.IN_PROGRESS, subtask.getStatus(), "Статус сабтаска не изменился");
    }

    @Test
    void SubtaskShouldEqualsSubtaskWithEqualsId() {
        Epic firstEpic = new Epic("Test first epic", "Test first epic description");
        firstEpic.setId(0);
        Subtask firstSubtask = new Subtask("Test first subtask", "Test first epic description", TaskStatus.NEW, firstEpic);
        firstSubtask.setId(1);
        Subtask secondSubtask = new Subtask("Test second subtask", "Test second subtask description", TaskStatus.NEW, firstEpic);
        secondSubtask.setId(1);
        assertEquals(firstSubtask, secondSubtask, "Сабтакски с одинаковым айди не равны.");
    }

    @Test
    void shouldNotChangeId(){
        Epic epic = new Epic("Test first epic", "Test first epic description", 0);
        Subtask subtask = new Subtask("Test first subtask", "Test first epic description", TaskStatus.NEW, epic, 1);
        subtask.setId(50);
        assertEquals(1, subtask.getId(), "Айди сабтаска изменился!");
    }

}