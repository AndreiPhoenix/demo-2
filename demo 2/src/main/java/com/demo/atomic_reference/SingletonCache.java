package com.demo.atomic_reference;

import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class SingletonCache<T> {
    private final AtomicReference<T> cache = new AtomicReference<>(null);
    private final Object lock = new Object();

    public T getOrCreate(ValueFactory<T> factory) {
        T currentValue = cache.get();
        if (currentValue != null) {
            return currentValue;
        }

        // Двойная проверка для оптимизации
        synchronized (lock) {
            currentValue = cache.get();
            if (currentValue != null) {
                return currentValue;
            }

            T newValue = factory.create();
            if (cache.compareAndSet(null, newValue)) {
                return newValue;
            } else {
                return cache.get();
            }
        }
    }

    public void clear() {
        cache.set(null);
    }

    public T getCurrentValue() {
        return cache.get();
    }

    @FunctionalInterface
    public interface ValueFactory<T> {
        T create();
    }

    // Демонстрация работы
    public static void main(String[] args) throws InterruptedException {
        SingletonCache<String> cache = new SingletonCache<>();
        ExecutorService executor = Executors.newFixedThreadPool(10);

        System.out.println("=== Демонстрация AtomicReference кэша ===");

        final int[] creationCount = {0};

        // Создаем 20 потоков, которые пытаются получить значение
        for (int i = 0; i < 20; i++) {
            final int threadId = i;
            executor.submit(() -> {
                String value = cache.getOrCreate(() -> {
                    creationCount[0]++;
                    System.out.println("Создание нового значения потоком " + threadId);
                    try {
                        Thread.sleep(100); // Имитация дорогой операции
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    }
                    return "Кэшированное значение от потока " + threadId;
                });

                System.out.println("Поток " + threadId + " получил: " + value);
            });
        }

        executor.shutdown();
        executor.awaitTermination(2, TimeUnit.SECONDS);

        System.out.println("\nКоличество созданий: " + creationCount[0]);
        System.out.println("Текущее значение в кэше: " + cache.getCurrentValue());
    }
}