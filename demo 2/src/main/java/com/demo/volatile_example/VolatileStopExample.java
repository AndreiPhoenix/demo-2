package com.demo.volatile_example;

public class VolatileStopExample {
    private volatile boolean stopRequested = false;

    public void startWorkerThread() {
        Thread workerThread = new Thread(() -> {
            int counter = 0;
            while (!stopRequested) {
                // Имитация работы
                System.out.println("Работает, счетчик: " + counter++);
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    break;
                }
            }
            System.out.println("Поток завершен корректно");
        });

        workerThread.start();
    }

    public void stopWorker() {
        stopRequested = true;
        System.out.println("Запрос на остановку отправлен");
    }

    public static void main(String[] args) throws InterruptedException {
        VolatileStopExample example = new VolatileStopExample();

        System.out.println("=== Демонстрация volatile остановки ===");
        example.startWorkerThread();

        // Даем потоку поработать 5 секунд
        Thread.sleep(5000);

        // Останавливаем поток
        example.stopWorker();

        Thread.sleep(1000); // Даем время для завершения
    }
}