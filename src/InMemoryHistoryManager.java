import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class InMemoryHistoryManager implements HistoryManager {

    private Node head;
    private Node tail;
    private final HashMap<Integer, Node> taskMap;

    InMemoryHistoryManager() {
        taskMap = new HashMap<>();
    }

    @Override
    public void add(Task task) {
        remove(task.getId());
        linkLast(task);
    }

    @Override
    public List<Task> getHistory() {
        return getTasks();
    }

    @Override
    public void remove(int id) {
         Node node = taskMap.remove(id);
         if (node != null) {
             removeNode(node);
         }
    }

    private void linkLast(Task task) {
        Node node = new Node(task, null, null);
        if (head == null) {
            head = node;
        } else {
            if (tail != null) {
                tail.next = node;
            }
            node.prev = tail;
        }
        tail = node;
        taskMap.put(task.getId(), node);
    }

    private void removeNode(Node node) {
        if (node.prev == null) {
            head = node.next;
            if (head != null) {
                head.prev = null;
            }
        } else {
            Node prevNode = node.prev;
            prevNode.next = node.next;
        }

        if (node.next == null) {
            tail = node.prev;
            if (tail != null) {
                tail.next = null;
            }
        } else {
            Node nextNode = node.next;
            nextNode.prev = node.prev;
        }
    }

    private ArrayList<Task> getTasks() {
        final ArrayList history = new ArrayList();
        Node node = head;
        while (node != null) {
            history.add(node.task);
            node = node.next;
        }
        return history;
    }

    private static class Node {
        Task task;
        Node prev;
        Node next;

        Node(Task task, Node prev, Node next) {
            this.task = task;
            this.prev = prev;
            this.next = next;
        }
    }
}
