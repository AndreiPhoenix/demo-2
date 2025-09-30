import java.util.Random;

public class Producer implements Runnable {
    private final BoundedBuffer<Integer> buffer;
    private final int itemsToProduce;
    private final Random random = new Random();

    public Producer(BoundedBuffer<Integer> buffer, int itemsToProduce) {
        this.buffer = buffer;
        this.itemsToProduce = itemsToProduce;
    }

    @Override
    public void run() {
        try {
            for (int i = 0; i < itemsToProduce; i++) {
                int item = random.nextInt(100);
                buffer.put(item);
                Thread.sleep(random.nextInt(100));
            }
            System.out.println(Thread.currentThread().getName() + " finished production");
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            System.out.println(Thread.currentThread().getName() + " was interrupted");
        }
    }
}