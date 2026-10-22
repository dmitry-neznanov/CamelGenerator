
package org.example.camel.registry;

import org.example.camel.config.SigtranConfig;
import org.example.camel.lifecycle.SigtranClientManager;

import java.util.Objects;
import java.util.concurrent.TimeUnit;

public final class SigtranClientRegistry {

    private static SigtranClientManager manager;
    private static SigtranConfig activeConfig;
    private static int users = 0;

    private SigtranClientRegistry() {
    }

    public static synchronized Lease acquire(
            SigtranConfig config
    ) throws Exception {

        Objects.requireNonNull(config);

        if (manager == null) {

            SigtranClientManager newManager =
                    new SigtranClientManager(config);

            try {
                newManager.start();
                newManager.awaitReady(15, TimeUnit.SECONDS);

            } catch (Exception e) {
                newManager.close();
                throw e;
            }

            manager = newManager;
            activeConfig = config;

            System.out.println(
                    "Shared SIGTRAN stack started"
            );

        } else if (!activeConfig.equals(config)) {

            throw new IllegalStateException(
                    "Shared SIGTRAN stack is already running "
                            + "with different configuration"
            );
        }

        users++;

        System.out.println(
                "SIGTRAN users: " + users
        );

        return new Lease(manager);
    }

    private static synchronized void release() {

        if (users <= 0) {
            throw new IllegalStateException(
                    "SIGTRAN reference counter is invalid"
            );
        }

        users--;

        System.out.println(
                "SIGTRAN users: " + users
        );

        if (users == 0) {

            try {
                manager.close();

                System.out.println(
                        "Shared SIGTRAN stack stopped"
                );

            } finally {
                manager = null;
                activeConfig = null;
            }
        }
    }

    public static final class Lease
            implements AutoCloseable {

        private final SigtranClientManager manager;
        private boolean closed = false;

        private Lease(SigtranClientManager manager) {
            this.manager = manager;
        }

        public synchronized SigtranClientManager getManager() {

            if (closed) {
                throw new IllegalStateException(
                        "SIGTRAN lease is already closed"
                );
            }

            return manager;
        }

        @Override
        public synchronized void close() {

            if (closed) {
                return;
            }

            closed = true;
            SigtranClientRegistry.release();
        }
    }
}
