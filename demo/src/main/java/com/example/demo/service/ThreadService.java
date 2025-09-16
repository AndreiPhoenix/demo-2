package com.example.demo.service;

import com.example.demo.task.CounterTask;
import org.springframework.stereotype.Service;

@Service
public class ThreadService {

    public void demonstrateThreads() {
        System.out.println("=== ЗАПУСК ДЕМОНСТРАЦИИ ПОТОКОВ ===");

        // Создание потоков через Thread с понятными именами
        Thread counterWorker = new Thread(new CounterTask("CounterWorker", 5), "CounterWorker");
        Thread loggerThread = new Thread(new CounterTask("LoggerThread", 3), "LoggerThread");

        System.out.println("Запускаем потоки...");

        // Запуск потоков
        counterWorker.start();
        loggerThread.start();

        try {
            // Ожидание завершения потоков
            counterWorker.join();
            loggerThread.join();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            System.out.println("Основной поток был прерван");
        }

        System.out.println("Все потоки завершили выполнение");

        // Вывод списка активных потоков
        printActiveThreads();
    }

    public void printActiveThreads() {
        System.out.println("\n=== СПИСОК АКТИВНЫХ ПОТОКОВ ===");

        // Получение всех активных потоков
        Thread[] threads = new Thread[Thread.activeCount()];
        int count = Thread.enumerate(threads);

        System.out.println("Всего активных потоков: " + count);

        for (int i = 0; i < count; i++) {
            Thread thread = threads[i];
            System.out.println("Поток: " + thread.getName() +
                    " (ID: " + thread.getId() +
                    ", Состояние: " + thread.getState() + ")");
        }
    }
}