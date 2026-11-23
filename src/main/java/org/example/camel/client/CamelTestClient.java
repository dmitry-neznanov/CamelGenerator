
package org.example.camel.client;

import org.example.camel.config.SigtranConfig;
import org.example.camel.generator.CapRequest;
import org.example.camel.registry.SigtranClientRegistry;

import java.util.Objects;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

public final class CamelTestClient implements AutoCloseable {

    private static final long DEFAULT_TIMEOUT_MS = 5000L;

    private final SigtranClientRegistry.Lease lease;
    private final AtomicBoolean closed = new AtomicBoolean(false);

    private CamelTestClient(SigtranClientRegistry.Lease lease) {
        this.lease = Objects.requireNonNull(lease);
    }

    public static CamelTestClient connect(SigtranConfig config)
            throws Exception {

        SigtranClientRegistry.Lease lease =
                SigtranClientRegistry.acquire(
                        Objects.requireNonNull(config)
                );

        return new CamelTestClient(lease);
    }

    public CapRequest sendInitialDPAsync(int serviceKey)
            throws Exception {

        ensureOpen();

        return lease.getManager()
                .getCapClient()
                .sendInitialDP(serviceKey);
    }

    public void awaitContinue(
            CapRequest request,
            long timeoutMs
    ) throws Exception {

        Objects.requireNonNull(request);

        if (timeoutMs <= 0) {
            throw new IllegalArgumentException(
                    "timeoutMs must be greater than zero"
            );
        }

        try {
            request.response().get(
                    timeoutMs,
                    TimeUnit.MILLISECONDS
            );

        } catch (InterruptedException e) {

            cancelRequest(request.dialogId());

            Thread.currentThread().interrupt();
            throw e;

        } catch (Exception e) {

            cancelRequest(request.dialogId());
            throw e;
        }
    }

    public InitialDPResult executeInitialDP(int serviceKey)
            throws Exception {

        return executeInitialDP(
                serviceKey,
                DEFAULT_TIMEOUT_MS
        );
    }

    public InitialDPResult executeInitialDP(
            int serviceKey,
            long timeoutMs
    ) throws Exception {

        ensureOpen();

        long startedNanos = System.nanoTime();

        CapRequest request = sendInitialDPAsync(serviceKey);

        awaitContinue(request, timeoutMs);

        long elapsedNanos =
                System.nanoTime() - startedNanos;

        return new InitialDPResult(
                request.dialogId(),
                serviceKey,
                elapsedNanos
        );
    }

    public void cancelRequest(long dialogId) {

        lease.getManager()
                .getCapClient()
                .cancelRequest(dialogId);
    }

    private void ensureOpen() {

        if (closed.get()) {
            throw new IllegalStateException(
                    "CamelTestClient is already closed"
            );
        }
    }

    @Override
    public void close() {

        if (closed.compareAndSet(false, true)) {
            lease.close();
        }
    }

    public record InitialDPResult(
            long dialogId,
            int serviceKey,
            long elapsedNanos
    ) {

        public double elapsedMs() {
            return elapsedNanos / 1_000_000.0;
        }
    }
}

