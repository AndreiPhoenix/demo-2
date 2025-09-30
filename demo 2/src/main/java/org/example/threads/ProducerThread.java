package org.example.threads;

import org.example.entity.Item;
import org.example.service.DataCollector;

import java.util.Random;

/**
 * Поток-производитель, который создает и добавляет элементы в DataCollector.
 */
public class ProducerThread extends Thread {
    private final DataCollector dataCollector;
    private final int itemsToProduce;
    private final Random random = new Random();

    public ProducerThread(DataCollector dataCollector, int itemsToProduce, String name) {
        super(name);
        this.dataCollector = dataCollector;
        this.itemsToProduce = itemsToProduce;
    }

    @Override
    public void run() {
        try {
            for (int i = 0; i < itemsToProduce; i++) {
                // Создаем элемент со случайным ключом (иногда дублирующимся для демонстрации проверки)
                String key = "key-" + (i % 15); // Создаем дубликаты
                Item item = new Item(key, "Data-" + i + "-from-" + getName());

                dataCollector.collectItem(item);

                // Имитируем работу
                Thread.sleep(random.nextInt(100));
            }
            System.out.println(getName() + " завершил производство элементов");
        } catch (InterruptedException e) {
            System.out.println(getName() + " был прерван");
            Thread.currentThread().interrupt();
        }
    }
}