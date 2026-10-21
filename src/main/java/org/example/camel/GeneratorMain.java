package org.example.camel;

import org.example.camel.config.SigtranConfig;
import org.example.camel.lifecycle.SigtranClientManager;

public class GeneratorMain {

    public static void main(String[] args) throws Exception {

        SigtranConfig config = SigtranConfig.localStub();

        try (SigtranClientManager manager =
                     new SigtranClientManager(config)) {

            manager.start();

            System.out.println("Press Enter to stop...");
            System.in.read();
        }
    }
}
