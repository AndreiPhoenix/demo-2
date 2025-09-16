package com.example.demo.task;

public class CounterTask implements Runnable {
    private final String threadName;
    private final int iterations;

    public CounterTask(String threadName, int iterations) {
        this.threadName = threadName;
        this.iterations = iterations;
    }

    @Override
    public void run() {
        for (int i = 1; i <= iterations; i++) {
            System.out.println(threadName + " - порядковый номер: " + i);
            try {
                Thread.sleep(1000); // Пауза 1 секунда между выводами
            } catch (InterruptedException e) {
                System.out.println(threadName + " был прерван");
                Thread.currentThread().interrupt();
                break;
            }
        }
        System.out.println(threadName + " завершил работу");
    }
}