package org.example;

import org.example.service.DataCollector;
import org.example.threads.ConsumerThread;
import org.example.threads.ProducerThread;

/**
 * Главный класс для демонстрации работы многопоточной системы сбора данных.
 * Проводит нагрузочное тестирование и сравнение производительности.
 */
public class Main {
    public static void main(String[] args) throws InterruptedException {
        System.out.println("=== Многопоточная система сбора данных ===");
        System.out.println("Демонстрация синхронизации с использованием synchronized, wait(), notify()\n");

        // Тест 1: Базовая функциональность с синхронизацией
        testSynchronizedSystem();

        // Небольшая пауза между тестами
        Thread.sleep(1000);

        // Тест 2: Сравнение с несинхронизированной версией (демонстрация проблем)
        testUnsynchronizedSystem();
    }

    private static void testSynchronizedSystem() throws InterruptedException {
        System.out.println("\n--- ТЕСТ 1: Синхронизированная система ---");
        DataCollector dataCollector = new DataCollector(50);

        // Создаем и запускаем потоки-производители
        ProducerThread producer1 = new ProducerThread(dataCollector, 30, "Producer-1");
        ProducerThread producer2 = new ProducerThread(dataCollector, 25, "Producer-2");
        ProducerThread producer3 = new ProducerThread(dataCollector, 20, "Producer-3");

        // Создаем и запускаем потоки-потребители
        ConsumerThread consumer1 = new ConsumerThread(dataCollector, "Consumer-1");
        ConsumerThread consumer2 = new ConsumerThread(dataCollector, "Consumer-2");

        long startTime = System.currentTimeMillis();

        // Запускаем все потоки
        producer1.start();
        producer2.start();
        producer3.start();
        consumer1.start();
        consumer2.start();

        // Ждем завершения производителей
        producer1.join();
        producer2.join();
        producer3.join();

        // Даем потребителям время обработать оставшиеся элементы
        Thread.sleep(1000);

        // Останавливаем потребителей
        dataCollector.setProcessingPaused(true);

        consumer1.join(2000);
        consumer2.join(2000);

        long endTime = System.currentTimeMillis();

        // Выводим статистику
        System.out.println("\n=== РЕЗУЛЬТАТЫ СИНХРОНИЗИРОВАННОЙ СИСТЕМЫ ===");
        System.out.println("Общее время выполнения: " + (endTime - startTime) + " мс");
        System.out.println("Обработано элементов: " + dataCollector.getProcessedCount());
        System.out.println("Размер очереди: " + dataCollector.getQueueSize());
        System.out.println("Уникальных ключей: " + dataCollector.getProcessedKeysSize());
    }

    private static void testUnsynchronizedSystem() {
        System.out.println("\n--- ТЕСТ 2: Демонстрация проблем без синхронизации ---");

        // Создаем простой несинхронизированный счетчик для демонстрации race condition
        UnsafeCounter unsafeCounter = new UnsafeCounter();

        // Запускаем несколько потоков, которые увеличивают счетчик
        Thread[] threads = new Thread[10];
        for (int i = 0; i < threads.length; i++) {
            threads[i] = new Thread(() -> {
                for (int j = 0; j < 1000; j++) {
                    unsafeCounter.increment();
                }
            });
            threads[i].start();
        }

        // Ждем завершения всех потоков
        for (Thread thread : threads) {
            try {
                thread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        System.out.println("Ожидаемое значение счетчика: 10000");
        System.out.println("Фактическое значение счетчика: " + unsafeCounter.getCount());
        System.out.println("Потерянные инкременты из-за гонки данных: " + (10000 - unsafeCounter.getCount()));
    }

    /**
     * Простой небезопасный счетчик для демонстрации проблем гонки данных.
     */
    static class UnsafeCounter {
        private int count = 0;

        // Метод НЕ синхронизирован - это вызывает race condition
        public void increment() {
            count++; // Эта операция не атомарна в многопоточной среде
        }

        public int getCount() {
            return count;
        }
    }
}