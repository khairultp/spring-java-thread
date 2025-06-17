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
public class BlockingController {

    private static final Logger LOGGER = LoggerFactory.getLogger(BlockingController.class);
    private static final int SLOW_PROCESS_NUM = 5;

    @GetMapping("/block")
    public String getBlockingData() throws Exception {
        var startTime = System.currentTimeMillis();
        LOGGER.info("Request received on thread {}", Thread.currentThread());

        try(ExecutorService executor = Executors.newFixedThreadPool(10)) {
            var futures = IntStream.range(0, SLOW_PROCESS_NUM)
                    .mapToObj(i -> executor.submit(this::slowTask))
                    .toList();

            for(var future : futures) {
                future.get();
            }
        }

        var duration = System.currentTimeMillis() - startTime;
        LOGGER.info("Request processed in {} ms", duration);

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
