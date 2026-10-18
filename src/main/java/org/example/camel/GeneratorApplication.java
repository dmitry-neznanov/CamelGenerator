package org.example.camel;

import org.example.camel.generator.SctpClient;

public class GeneratorApplication {

    public static void main(String[] args) throws Exception {
        SctpClient client = new SctpClient();
        client.start();

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            try {
                client.stop();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }));

        new java.util.concurrent.CountDownLatch(1).await();
    }
}
