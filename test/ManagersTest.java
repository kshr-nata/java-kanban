import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ManagersTest {
    @Test
    void historyManagerShouldBeNotNull() {
        HistoryManager historyManager = Managers.getDefaultHistory();
        assertNotNull(historyManager, "historyManager is null!");
    }

    @Test
    void TaskManagerShouldBeNotNull() {
        TaskManager taskManager = Managers.getDefault();
        assertNotNull(taskManager, "taskManager is null!");
    }

}