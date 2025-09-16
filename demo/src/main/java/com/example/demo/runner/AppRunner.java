package com.example.demo.runner;

import com.example.demo.service.ThreadService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class AppRunner implements CommandLineRunner {

    private final ThreadService threadService;

    public AppRunner(ThreadService threadService) {
        this.threadService = threadService;
    }

    @Override
    public void run(String... args) throws Exception {
        System.out.println("🚀 ПРИЛОЖЕНИЕ ЗАПУЩЕНО");
        System.out.println("Демонстрация работы с Thread и Runnable\n");

        threadService.demonstrateThreads();

        System.out.println("\n✅ ДЕМОНСТРАЦИЯ ЗАВЕРШЕНА");
    }
}