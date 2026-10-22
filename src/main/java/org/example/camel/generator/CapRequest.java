package org.example.camel.generator;

import java.util.concurrent.CompletableFuture;

public record CapRequest(
        long dialogId,
        CompletableFuture<Void> response
) {
}
