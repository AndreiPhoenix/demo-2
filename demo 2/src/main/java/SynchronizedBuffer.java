import java.util.LinkedList;
import java.util.Queue;

public class SynchronizedBuffer implements BufferInterface {
    private final Queue<Integer> buffer;
    private final int capacity;

    public SynchronizedBuffer(int capacity) {
        this.buffer = new LinkedList<>();
        this.capacity = capacity;
    }

    @Override
    public synchronized void put(Integer item) throws InterruptedException {
        while (buffer.size() == capacity) {
            wait();
        }
        buffer.offer(item);
        notifyAll();
    }

    @Override
    public synchronized Integer take() throws InterruptedException {
        while (buffer.isEmpty()) {
            wait();
        }
        Integer item = buffer.poll();
        notifyAll();
        return item;
    }

    @Override
    public synchronized int size() {
        return buffer.size();
    }
}