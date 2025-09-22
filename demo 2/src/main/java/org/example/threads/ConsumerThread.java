package org.example.threads;

import org.example.entity.Item;
import org.example.service.DataCollector;

import java.util.Random;

/**
 * Поток-потребитель, который обрабатывает элементы из DataCollector.
 */
public class ConsumerThread extends Thread {
    private final DataCollector dataCollector;
    private final Random random = new Random();

    public ConsumerThread(DataCollector dataCollector, String name) {
        super(name);
        this.dataCollector = dataCollector;
    }

    @Override
    public void run() {
        try {
            while (true) {
                Item item = dataCollector.processNextItem();
                if (item == null) {
                    // Если элементов нет и достигнуто условие завершения
                    if (dataCollector.getProcessedCount() > 100) {
                        break;
                    }
                    continue;
                }

                // Имитируем обработку элемента
                Thread.sleep(random.nextInt(150));

                // Периодически имитируем "тяжелую" обработку
                if (random.nextInt(10) == 0) {
                    Thread.sleep(200);
                }
            }
            System.out.println(getName() + " завершил обработку");
        } catch (InterruptedException e) {
            System.out.println(getName() + " был прерван");
            Thread.currentThread().interrupt();
        }
    }
}