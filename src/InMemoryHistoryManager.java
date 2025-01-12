import java.util.ArrayList;
import java.util.List;

public class InMemoryHistoryManager implements HistoryManager {

    private final List<Task> history;

    InMemoryHistoryManager(){
        history = new ArrayList<>();
    }
    @Override
    public void add(Task task) {
        history.add(0, task);
        if (history.size() > 10) {
                history.remove(10);
            }

    }

    @Override
    public List<Task> getHistory() {
        return history;
    }
}
