
package org.example.camel;

import org.example.camel.config.SigtranConfig;
import org.example.camel.generator.CapRequest;
import org.example.camel.lifecycle.SigtranClientManager;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

public class GeneratorMain {

    public static void main(String[] args) throws Exception {

        SigtranConfig config = SigtranConfig.localStub();

        try (SigtranClientManager manager =
                     new SigtranClientManager(config)) {

            manager.start();
            manager.awaitReady(15, TimeUnit.SECONDS);

            System.out.println("SIGTRAN stack is READY");

            int requestsCount = 10;

            ExecutorService executor =
                    Executors.newFixedThreadPool(requestsCount);

            try {
                List<Future<Boolean>> results = new ArrayList<>();

                for (int i = 0; i < requestsCount; i++) {

                    results.add(executor.submit(() -> {

                        CapRequest request = null;

                        try {
                            request = manager.getCapClient()
                                    .sendInitialDP(10);

                            request.response()
                                    .get(5, TimeUnit.SECONDS);

                            System.out.println(
                                    "SUCCESS: Dialog ID="
                                            + request.dialogId()
                            );

                            return true;

                        } catch (Exception e) {

                            if (request != null) {
                                manager.getCapClient()
                                        .cancelRequest(request.dialogId());
                            }

                            System.err.println(
                                    "FAILED: " + e.getMessage()
                            );

                            return false;
                        }
                    }));
                }

                int success = 0;
                int failed = 0;

                for (Future<Boolean> result : results) {

                    if (result.get()) {
                        success++;
                    } else {
                        failed++;
                    }
                }

                System.out.println("=== TEST RESULTS ===");
                System.out.println("Total: " + requestsCount);
                System.out.println("Success: " + success);
                System.out.println("Failed: " + failed);

            } finally {
                executor.shutdown();

                if (!executor.awaitTermination(10, TimeUnit.SECONDS)) {
                    executor.shutdownNow();
                }
            }

            System.out.println("Press Enter to stop...");
            System.in.read();
        }
    }
}
