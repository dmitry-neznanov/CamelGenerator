
package org.example.camel.generator;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

public class CapRequestTracker {

    private final ConcurrentHashMap<Long, CompletableFuture<Void>>
            pendingRequests = new ConcurrentHashMap<>();

    public CompletableFuture<Void> register(long dialogId) {

        CompletableFuture<Void> future = new CompletableFuture<>();

        CompletableFuture<Void> existing =
                pendingRequests.putIfAbsent(dialogId, future);

        if (existing != null) {
            throw new IllegalStateException(
                    "Dialog already registered: " + dialogId
            );
        }

        return future;
    }

    public void complete(long dialogId) {

        CompletableFuture<Void> future =
                pendingRequests.remove(dialogId);

        if (future != null) {
            future.complete(null);
        }
    }

    public void fail(long dialogId, Throwable error) {

        CompletableFuture<Void> future =
                pendingRequests.remove(dialogId);

        if (future != null) {
            future.completeExceptionally(error);
        }
    }

    public void cancel(long dialogId) {

        CompletableFuture<Void> future =
                pendingRequests.remove(dialogId);

        if (future != null) {
            future.cancel(false);
        }
    }

    public int getPendingCount() {
        return pendingRequests.size();
    }
}
