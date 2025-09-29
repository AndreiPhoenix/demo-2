import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

public class BoundedBuffer<T> {
    private final Queue<T> buffer;
    private final int capacity;
    private final ReentrantLock lock;
    private final Condition notEmpty;
    private final Condition notFull;

    public BoundedBuffer(int capacity) {
        if (capacity <= 0) {
            throw new IllegalArgumentException("Capacity must be positive");
        }
        this.buffer = new LinkedList<>();
        this.capacity = capacity;
        this.lock = new ReentrantLock(true);
        this.notEmpty = lock.newCondition();
        this.notFull = lock.newCondition();
    }

    public void put(T item) throws InterruptedException {
        lock.lock();
        try {
            while (buffer.size() == capacity) {
                System.out.println(Thread.currentThread().getName() + ": Buffer full, waiting...");
                notFull.await();
            }

            buffer.offer(item);
            System.out.println(Thread.currentThread().getName() + " produced: " + item +
                    " (size: " + buffer.size() + ")");

            notEmpty.signalAll();
        } finally {
            lock.unlock();
        }
    }

    public T take() throws InterruptedException {
        lock.lock();
        try {
            while (buffer.isEmpty()) {
                System.out.println(Thread.currentThread().getName() + ": Buffer empty, waiting...");
                notEmpty.await();
            }

            T item = buffer.poll();
            System.out.println(Thread.currentThread().getName() + " consumed: " + item +
                    " (size: " + buffer.size() + ")");

            notFull.signalAll();
            return item;
        } finally {
            lock.unlock();
        }
    }

    public int size() {
        lock.lock();
        try {
            return buffer.size();
        } finally {
            lock.unlock();
        }
    }

    public boolean isEmpty() {
        lock.lock();
        try {
            return buffer.isEmpty();
        } finally {
            lock.unlock();
        }
    }

    public boolean isFull() {
        lock.lock();
        try {
            return buffer.size() == capacity;
        } finally {
            lock.unlock();
        }
    }
}