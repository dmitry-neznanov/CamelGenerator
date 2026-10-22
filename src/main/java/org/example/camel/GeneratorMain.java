
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

            Thread.sleep(3000);

            manager.printM3uaState();

            System.out.println("Press Enter to stop...");
            System.in.read();
        }
    }
}
