package org.example.entity;

/**
 * Класс представляет элемент данных, который будет собираться и обрабатываться.
 */
public class Item {
    private final String key; // Уникальный идентификатор элемента
    private final String data; // Данные элемента

    public Item(String key, String data) {
        this.key = key;
        this.data = data;
    }

    public String getKey() {
        return key;
    }

    public String getData() {
        return data;
    }

    @Override
    public String toString() {
        return "Item{key='" + key + "', data='" + data + "'}";
    }
}