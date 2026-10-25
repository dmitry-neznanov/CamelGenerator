package org.example.camel.config;

import java.nio.file.Files;
import java.nio.file.Path;

public final class SigtranPaths {

    private SigtranPaths() {
    }

    public static Path getConfigDirectory() {

        String configured = System.getProperty(
                "camel.sigtran.config.dir"
        );

        Path directory;

        if (configured != null && !configured.isBlank()) {
            directory = Path.of(configured);
        } else {
            directory = Path.of(
                    System.getProperty("user.home"),
                    "camel-sigtran",
                    "generator"
            );
        }

        try {
            Files.createDirectories(directory);
        } catch (Exception e) {
            throw new IllegalStateException(
                    "Cannot create SIGTRAN config directory: " + directory,
                    e
            );
        }

        return directory.toAbsolutePath();
    }
}