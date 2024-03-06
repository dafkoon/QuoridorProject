package Controller;
import java.util.PriorityQueue;
import java.util.Comparator;
public class MaxHeap {
    private final PriorityQueue<String> queue;
    public MaxHeap() {
        this.queue = new PriorityQueue<>(Comparator.reverseOrder());
    }
}
