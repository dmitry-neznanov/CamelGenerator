
package org.example.camel.client;

import org.example.camel.config.SigtranConfig;

public final class CamelTestRuntime {

    private static volatile CamelTestClient client;

    private CamelTestRuntime() {
    }

    public static synchronized void start(
            SigtranConfig config
    ) throws Exception {

        if (client != null) {
            throw new IllegalStateException(
                    "CamelTestRuntime is already started"
            );
        }

        client = CamelTestClient.connect(config);

        System.out.println(
                "CamelTestRuntime started"
        );
    }

    public static CamelTestClient getClient() {

        CamelTestClient current = client;

        if (current == null) {
            throw new IllegalStateException(
                    "CamelTestRuntime is not initialized"
            );
        }

        return current;
    }

    public static synchronized void stop() {

        CamelTestClient current = client;

        if (current == null) {
            return;
        }

        try {
            current.close();
        } finally {
            client = null;
        }

        System.out.println(
                "CamelTestRuntime stopped"
        );
    }
}

