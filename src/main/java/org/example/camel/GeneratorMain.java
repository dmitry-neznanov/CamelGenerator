
package org.example.camel;

import org.example.camel.config.SigtranConfig;
import org.example.camel.registry.SigtranClientRegistry;

import java.util.concurrent.TimeUnit;

public class GeneratorMain {

    public static void main(String[] args) throws Exception {

        SigtranConfig config = SigtranConfig.localStub();

        try (
                SigtranClientRegistry.Lease client1 =
                        SigtranClientRegistry.acquire(config);

                SigtranClientRegistry.Lease client2 =
                        SigtranClientRegistry.acquire(config)
        ) {

            System.out.println(
                    "Same manager: "
                            + (client1.getManager()
                            == client2.getManager())
            );

            var request = client1.getManager()
                    .getCapClient()
                    .sendInitialDP(10);

            request.response().get(5, TimeUnit.SECONDS);

            System.out.println(
                    "SUCCESS: InitialDP -> Continue. Dialog ID="
                            + request.dialogId()
            );
        }
    }
}
