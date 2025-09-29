package org.example.service;

import org.example.entity.Item;

import java.util.*;

/**
 * Класс для сбора и обработки данных в многопоточной среде.
 * Обеспечивает синхронизацию доступа к общим ресурсам.
 */
public class DataCollector {
    // Общие ресурсы, требующие синхронизации
    private final Set<String> processedKeys; // Множество обработанных ключей
    private int processedCount; // Счетчик обработанных элементов
    private final List<Item> collectedItems; // Список собранных элементов

    // Флаг для управления ожиданием потоков (например, при достижении лимита)
    private boolean processingPaused = false;
    private final int maxQueueSize; // Максимальный размер очереди перед паузой

    public DataCollector(int maxQueueSize) {
        this.processedKeys = Collections.synchronizedSet(new HashSet<>());
        this.collectedItems = Collections.synchronizedList(new ArrayList<>());
        this.processedCount = 0;
        this.maxQueueSize = maxQueueSize;
    }

    public DataCollector() {
        this(100); // Значение по умолчанию
    }

    /**
     * Добавляет элемент в коллекцию, если он еще не был обработан.
     * Использует synchronized для защиты от гонки данных.
     *
     * @param item элемент для добавления
     * @return true если элемент был добавлен, false если уже был обработан
     */
    public synchronized boolean collectItem(Item item) {
        // Проверяем, не обработан ли уже элемент
        if (isAlreadyProcessed(item.getKey())) {
            System.out.println(Thread.currentThread().getName() + " - Элемент с key='" + item.getKey() + "' уже обработан. Пропускаем.");
            return false;
        }

        // Ожидаем, если очередь достигла лимита (использование wait())
        while (processingPaused || collectedItems.size() >= maxQueueSize) {
            try {
                System.out.println(Thread.currentThread().getName() + " - Очередь заполнена. Ожидание...");
                wait(); // Поток ждет, пока не будет вызван notifyAll()
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                System.out.println("Поток был прерван во время ожидания");
                return false;
            }
        }

        // Добавляем элемент в коллекцию
        collectedItems.add(item);
        processedKeys.add(item.getKey());
        System.out.println(Thread.currentThread().getName() + " - Добавлен: " + item);

        // Уведомляем все ожидающие потоки, что состояние изменилось
        notifyAll();
        return true;
    }

    /**
     * Извлекает и обрабатывает следующий элемент из коллекции.
     * Использует synchronized для атомарности операций.
     *
     * @return обработанный элемент или null если коллекция пуста
     */
    public synchronized Item processNextItem() {
        // Ожидаем, пока появятся элементы для обработки
        while (collectedItems.isEmpty()) {
            if (processedCount > 100) { // Условие завершения для демонстрации
                return null;
            }
            try {
                System.out.println(Thread.currentThread().getName() + " - Нет элементов для обработки. Ожидание...");
                wait(1000); // Таймаут 1 секунда для избежания бесконечного ожидания
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                return null;
            }
        }

        // Извлекаем элемент для обработки
        Item item = collectedItems.remove(0);
        incrementProcessed();
        System.out.println(Thread.currentThread().getName() + " - Обработан: " + item + " | Всего обработано: " + processedCount);

        // Если очередь освободилась, уведомляем потоки, ожидающие добавления
        if (collectedItems.size() < maxQueueSize / 2) {
            processingPaused = false;
            notifyAll(); // Уведомляем все ожидающие потоки
        }

        return item;
    }

    /**
     * Увеличивает счетчик обработанных элементов.
     * synchronized обеспечивает атомарность операции.
     */
    private synchronized void incrementProcessed() {
        processedCount++; // Эта операция теперь атомарна
        // Можно добавить дополнительную логику при достижении определенного счетчика
        if (processedCount % 10 == 0) {
            System.out.println("=== Достигнуто " + processedCount + " обработанных элементов ===");
        }
    }

    /**
     * Проверяет, был ли элемент с заданным ключом уже обработан.
     * synchronized обеспечивает консистентность данных.
     *
     * @param key ключ для проверки
     * @return true если элемент уже обработан
     */
    public synchronized boolean isAlreadyProcessed(String key) {
        return processedKeys.contains(key);
    }

    /**
     * Устанавливает флаг паузы обработки.
     * Может использоваться для управления потоками извне.
     */
    public synchronized void setProcessingPaused(boolean paused) {
        this.processingPaused = paused;
        if (!paused) {
            notifyAll(); // Возобновляем все ожидающие потоки
        }
    }

    // Методы для получения статистики (тоже synchronized для безопасности)
    public synchronized int getProcessedCount() {
        return processedCount;
    }

    public synchronized int getQueueSize() {
        return collectedItems.size();
    }

    public synchronized int getProcessedKeysSize() {
        return processedKeys.size();
    }
}