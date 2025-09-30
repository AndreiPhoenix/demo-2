import java.util.Random;

public class Consumer implements Runnable {
    private final BoundedBuffer<Integer> buffer;
    private final int itemsToConsume;
    private final Random random = new Random();

    public Consumer(BoundedBuffer<Integer> buffer, int itemsToConsume) {
        this.buffer = buffer;
        this.itemsToConsume = itemsToConsume;
    }

    @Override
    public void run() {
        try {
            for (int i = 0; i < itemsToConsume; i++) {
                Integer item = buffer.take();
                Thread.sleep(random.nextInt(150));
            }
            System.out.println(Thread.currentThread().getName() + " finished consumption");
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            System.out.println(Thread.currentThread().getName() + " was interrupted");
        }
    }
}