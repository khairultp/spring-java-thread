package com.khairul.thread.virtual.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Duration;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.IntStream;

@RestController
public class NonBlockingController {

    private static final Logger LOGGER = LoggerFactory.getLogger(NonBlockingController.class);
    private static final int SLOW_PROCESS_NUM = 5;

    @GetMapping("/non-block")
    public String getNonBlockingData() throws Exception {
        var startTime = System.currentTimeMillis();
        LOGGER.debug("Request received on thread {}", Thread.currentThread());

        try(ExecutorService executor = Executors.newVirtualThreadPerTaskExecutor()) {
            var futures = IntStream.range(0, SLOW_PROCESS_NUM)
                    .mapToObj(i -> executor.submit(this::slowTask))
                    .toList();

            LOGGER.debug("Total Slow process : {}", futures.size());

            for(var future : futures) {
                future.get();
            }
        }

        var duration = System.currentTimeMillis() - startTime;
        LOGGER.debug("Request processed in {} ms", duration);

        return "Aggregated data processed in %d ms".formatted(duration);
    }

    private String slowTask() {
        try {
            LOGGER.debug("Task running on thread {}", Thread.currentThread());
            Thread.sleep(Duration.ofSeconds(1));
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        return "done";
    }
}
