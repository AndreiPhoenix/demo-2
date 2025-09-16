public class ThreadStatesDemo {

    public static void main(String[] args) throws InterruptedException {
        System.out.println("=== Демонстрация состояний потоков ===");

        // Поток 1: NEW -> RUNNABLE -> TERMINATED
        Thread thread1 = new Thread(() -> {
            System.out.println("Поток 1: выполняется простая задача");
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });

        System.out.println("Поток 1 состояние после создания: " + thread1.getState()); // NEW

        // Поток 2: NEW -> RUNNABLE -> TIMED_WAITING -> RUNNABLE -> TERMINATED
        Thread thread2 = new Thread(() -> {
            System.out.println("Поток 2: начинаю работу");
            try {
                Thread.sleep(2000); // TIMED_WAITING
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.println("Поток 2: завершаю работу");
        });

        // Поток 3: NEW -> RUNNABLE -> BLOCKED -> RUNNABLE -> TERMINATED
        final Object lock = new Object();
        Thread thread3 = new Thread(() -> {
            synchronized (lock) {
                System.out.println("Поток 3: захватил блокировку");
                try {
                    Thread.sleep(1500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });

        // Поток для создания BLOCKED состояния
        Thread thread4 = new Thread(() -> {
            System.out.println("Поток 4: пытаюсь захватить блокировку...");
            synchronized (lock) {
                System.out.println("Поток 4: захватил блокировку");
            }
        });

        // Запускаем потоки и наблюдаем за состояниями
        thread1.start();
        System.out.println("Поток 1 состояние после start(): " + thread1.getState()); // RUNNABLE

        thread2.start();
        Thread.sleep(50); // Даем время потоку перейти в TIMED_WAITING
        System.out.println("Поток 2 состояние после sleep(): " + thread2.getState()); // TIMED_WAITING

        thread3.start();
        Thread.sleep(50); // Даем время потоку запуститься
        System.out.println("Поток 3 состояние после start(): " + thread3.getState()); // RUNNABLE или TIMED_WAITING

        // Запускаем поток, который будет заблокирован
        thread4.start();
        Thread.sleep(50); // Даем время потоку попытаться захватить блокировку
        System.out.println("Поток 4 состояние (ожидание блокировки): " + thread4.getState()); // BLOCKED

        // Используем join() для синхронизации
        System.out.println("\n=== Использование join() для синхронизации ===");

        thread1.join(); // Ждем завершения потока 1
        System.out.println("Поток 1 состояние после join(): " + thread1.getState()); // TERMINATED

        // Проверяем состояния во время выполнения
        Thread.sleep(500);
        System.out.println("Поток 2 состояние через 500мс: " + thread2.getState());
        System.out.println("Поток 3 состояние через 500мс: " + thread3.getState());
        System.out.println("Поток 4 состояние через 500мс: " + thread4.getState());

        // Ждем завершения всех потоков
        thread2.join();
        thread3.join();
        thread4.join();

        System.out.println("\n=== Финальные состояния ===");
        System.out.println("Поток 1: " + thread1.getState()); // TERMINATED
        System.out.println("Поток 2: " + thread2.getState()); // TERMINATED
        System.out.println("Поток 3: " + thread3.getState()); // TERMINATED
        System.out.println("Поток 4: " + thread4.getState()); // TERMINATED

        // Демонстрация WAITING состояния
        System.out.println("\n=== Демонстрация WAITING состояния ===");
        Object waitLock = new Object();
        Thread waitingThread = new Thread(() -> {
            synchronized (waitLock) {
                try {
                    waitLock.wait(); // WAITING
                    System.out.println("Поток WAITING: пробужден");
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });

        waitingThread.start();
        Thread.sleep(100);
        System.out.println("WAITING поток состояние: " + waitingThread.getState()); // WAITING

        // Пробуждаем поток
        synchronized (waitLock) {
            waitLock.notify();
        }

        waitingThread.join();
        System.out.println("WAITING поток после завершения: " + waitingThread.getState()); // TERMINATED
    }
}