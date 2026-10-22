
package org.example.camel;

import org.example.camel.config.SigtranConfig;
import org.example.camel.generator.CapRequest;
import org.example.camel.lifecycle.SigtranClientManager;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class GeneratorMain {

    public static void main(String[] args) throws Exception {

        SigtranConfig config = SigtranConfig.localStub();

        try (SigtranClientManager manager =
                     new SigtranClientManager(config)) {

            manager.start();

            // Ожидаем готовность M3UA
            manager.awaitReady(15, TimeUnit.SECONDS);

            System.out.println("ASP is ACTIVE");

            // Только для диагностики
            Thread.sleep(1000);

            System.out.println("Sending InitialDP...");

            System.out.println("SIGTRAN stack is READY");

            CapRequest request =
                    manager.getCapClient().sendInitialDP(10);

            System.out.println(
                    "Waiting for Continue. Dialog ID="
                            + request.dialogId()
            );

            try {

                request.response().get(5, TimeUnit.SECONDS);

                System.out.println(
                        "SUCCESS: InitialDP -> Continue. Dialog ID="
                                + request.dialogId()
                );

            } catch (TimeoutException e) {

                manager.getCapClient()
                        .cancelRequest(request.dialogId());

                System.err.println(
                        "TIMEOUT: Continue not received. Dialog ID="
                                + request.dialogId()
                );
            }

            System.out.println("Press Enter to stop...");
            System.in.read();
        }
    }
}
