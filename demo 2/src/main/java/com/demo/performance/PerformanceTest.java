package com.demo.performance;

import com.demo.atomic_integer.ThreadSafeCounter;
import com.demo.atomic_reference.SingletonCache;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public class PerformanceTest {

    public static void testAtomicIntegerPerformance() throws InterruptedException {
        System.out.println("=== Тестирование производительности AtomicInteger ===");

        ThreadSafeCounter counter = new ThreadSafeCounter();
        int threadCount = 100;
        int operationsPerThread = 100000;

        ExecutorService executor = Executors.newFixedThreadPool(threadCount);

        long startTime = System.currentTimeMillis();

        for (int i = 0; i < threadCount; i++) {
            executor.submit(() -> {
                for (int j = 0; j < operationsPerThread; j++) {
                    counter.increment();
                }
            });
        }

        executor.shutdown();
        executor.awaitTermination(1, TimeUnit.MINUTES);

        long endTime = System.currentTimeMillis();

        System.out.println("Время выполнения: " + (endTime - startTime) + " мс");
        System.out.println("Ожидаемое значение: " + (threadCount * operationsPerThread));
        System.out.println("Фактическое значение: " + counter.getValue());
    }

    public static void testSingletonCachePerformance() throws InterruptedException {
        System.out.println("\n=== Тестирование производительности SingletonCache ===");

        SingletonCache<String> cache = new SingletonCache<>();
        int threadCount = 50;
        int operationsPerThread = 1000;

        ExecutorService executor = Executors.newFixedThreadPool(threadCount);
        AtomicInteger creationCount = new AtomicInteger(0);

        long startTime = System.currentTimeMillis();

        for (int i = 0; i < threadCount; i++) {
            executor.submit(() -> {
                for (int j = 0; j < operationsPerThread; j++) {
                    cache.getOrCreate(() -> {
                        creationCount.incrementAndGet();
                        return "expensive_value";
                    });
                }
            });
        }

        executor.shutdown();
        executor.awaitTermination(1, TimeUnit.MINUTES);

        long endTime = System.currentTimeMillis();

        System.out.println("Время выполнения: " + (endTime - startTime) + " мс");
        System.out.println("Количество созданий: " + creationCount.get());
        System.out.println("Ожидаемое количество созданий: 1");
    }

    public static void testVolatilePerformance() throws InterruptedException {
        System.out.println("\n=== Тестирование производительности volatile ===");

        class VolatileWorker implements Runnable {
            private volatile boolean stop = false;
            private long counter = 0;

            public void run() {
                while (!stop) {
                    counter++;
                    // Небольшая пауза для имитации работы
                    if (counter % 1000 == 0) {
                        Thread.yield();
                    }
                }
            }

            public void stop() {
                stop = true;
            }
        }

        VolatileWorker worker = new VolatileWorker();
        Thread thread = new Thread(worker);

        long startTime = System.currentTimeMillis();
        thread.start();

        // Даем поработать 2 секунды
        Thread.sleep(2000);

        worker.stop();
        thread.join();

        long endTime = System.currentTimeMillis();

        System.out.println("Время работы: " + (endTime - startTime) + " мс");
        System.out.println("Счетчик: " + worker.counter);
    }

    public static void main(String[] args) throws InterruptedException {
        testAtomicIntegerPerformance();
        testSingletonCachePerformance();
        testVolatilePerformance();
    }
}