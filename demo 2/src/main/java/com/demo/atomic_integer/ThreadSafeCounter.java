package com.demo.atomic_integer;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class ThreadSafeCounter {
    private final AtomicInteger counter = new AtomicInteger(0);

    public void increment() {
        counter.incrementAndGet();
    }

    public int getValue() {
        return counter.get();
    }

    public void reset() {
        counter.set(0);
    }

    // Демонстрация работы в многопоточной среде
    public static void main(String[] args) throws InterruptedException {
        ThreadSafeCounter safeCounter = new ThreadSafeCounter();
        ExecutorService executor = Executors.newFixedThreadPool(10);

        System.out.println("=== Демонстрация AtomicInteger счетчика ===");

        // Создаем 1000 задач для инкремента счетчика
        for (int i = 0; i < 1000; i++) {
            executor.submit(() -> {
                safeCounter.increment();
            });
        }

        executor.shutdown();
        executor.awaitTermination(1, TimeUnit.MINUTES);

        System.out.println("Ожидаемое значение: 1000");
        System.out.println("Фактическое значение: " + safeCounter.getValue());

        // Демонстрация проблемы без AtomicInteger
        demonstrateNonAtomicProblem();
    }

    private static void demonstrateNonAtomicProblem() throws InterruptedException {
        System.out.println("\n=== Демонстрация проблемы без AtomicInteger ===");

        class UnsafeCounter {
            private int counter = 0;

            public void increment() {
                counter++; // Не атомарная операция!
            }

            public int getValue() {
                return counter;
            }
        }

        UnsafeCounter unsafeCounter = new UnsafeCounter();
        ExecutorService executor = Executors.newFixedThreadPool(10);

        for (int i = 0; i < 1000; i++) {
            executor.submit(() -> {
                unsafeCounter.increment();
            });
        }

        executor.shutdown();
        executor.awaitTermination(1, TimeUnit.MINUTES);

        System.out.println("Ожидаемое значение: 1000");
        System.out.println("Фактическое значение (небезопасный): " + unsafeCounter.getValue());
    }
}