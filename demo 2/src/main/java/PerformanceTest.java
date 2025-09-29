public class PerformanceTest {
    private static final int BUFFER_CAPACITY = 10;
    private static final int PRODUCER_COUNT = 3;
    private static final int CONSUMER_COUNT = 3;
    private static final int ITEMS_PER_PRODUCER = 20;

    public static void main(String[] args) throws InterruptedException {
        System.out.println("=== Testing ReentrantLock Implementation ===");
        testReentrantLockImplementation();

        System.out.println("\n=== Performance Comparison ===");
        comparePerformance();
    }

    private static void testReentrantLockImplementation() throws InterruptedException {
        BoundedBuffer<Integer> buffer = new BoundedBuffer<>(BUFFER_CAPACITY);

        Thread[] producers = new Thread[PRODUCER_COUNT];
        Thread[] consumers = new Thread[CONSUMER_COUNT];

        // Создаем producer'ов
        for (int i = 0; i < PRODUCER_COUNT; i++) {
            producers[i] = new Thread(new Producer(buffer, ITEMS_PER_PRODUCER),
                    "Producer-" + (i + 1));
        }

        // Создаем consumer'ов
        for (int i = 0; i < CONSUMER_COUNT; i++) {
            consumers[i] = new Thread(new Consumer(buffer,
                    ITEMS_PER_PRODUCER * PRODUCER_COUNT / CONSUMER_COUNT),
                    "Consumer-" + (i + 1));
        }

        long startTime = System.currentTimeMillis();

        // Запускаем consumer'ов
        for (Thread consumer : consumers) {
            consumer.start();
        }

        // Запускаем producer'ов
        for (Thread producer : producers) {
            producer.start();
        }

        // Ждем завершения producer'ов
        for (Thread producer : producers) {
            producer.join();
        }

        // Ждем завершения consumer'ов
        for (Thread consumer : consumers) {
            consumer.join();
        }

        long endTime = System.currentTimeMillis();

        System.out.println("All threads completed");
        System.out.println("Final buffer size: " + buffer.size());
        System.out.println("Total execution time: " + (endTime - startTime) + "ms");
    }

    private static void comparePerformance() throws InterruptedException {
        int iterations = 5;

        long reentrantLockTime = 0;
        long synchronizedTime = 0;

        for (int i = 0; i < iterations; i++) {
            reentrantLockTime += testReentrantLockPerformance();
            synchronizedTime += testSynchronizedPerformance();
        }

        System.out.println("Average ReentrantLock time: " + (reentrantLockTime / iterations) + "ms");
        System.out.println("Average Synchronized time: " + (synchronizedTime / iterations) + "ms");

        double difference = ((double) synchronizedTime - reentrantLockTime) / reentrantLockTime * 100;
        System.out.printf("ReentrantLock is %.2f%% %s\n",
                Math.abs(difference),
                difference > 0 ? "faster" : "slower");
    }

    private static long testReentrantLockPerformance() throws InterruptedException {
        BoundedBuffer<Integer> buffer = new BoundedBuffer<>(BUFFER_CAPACITY);
        return runPerformanceTest((BufferInterface) buffer);
    }

    private static long testSynchronizedPerformance() throws InterruptedException {
        SynchronizedBuffer buffer = new SynchronizedBuffer(BUFFER_CAPACITY);
        return runPerformanceTest(buffer);
    }

    private static long runPerformanceTest(BufferInterface buffer) throws InterruptedException {
        Thread[] producers = new Thread[PRODUCER_COUNT];
        Thread[] consumers = new Thread[CONSUMER_COUNT];

        for (int i = 0; i < PRODUCER_COUNT; i++) {
            producers[i] = new Thread(new Producer((BoundedBuffer<Integer>) buffer, ITEMS_PER_PRODUCER),
                    "Producer-" + (i + 1));
        }

        for (int i = 0; i < CONSUMER_COUNT; i++) {
            consumers[i] = new Thread(new Consumer((BoundedBuffer<Integer>) buffer,
                    ITEMS_PER_PRODUCER * PRODUCER_COUNT / CONSUMER_COUNT),
                    "Consumer-" + (i + 1));
        }

        long startTime = System.currentTimeMillis();

        for (Thread consumer : consumers) {
            consumer.start();
        }
        for (Thread producer : producers) {
            producer.start();
        }

        for (Thread producer : producers) {
            producer.join();
        }
        for (Thread consumer : consumers) {
            consumer.join();
        }

        return System.currentTimeMillis() - startTime;
    }
}