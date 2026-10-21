
package org.example.camel.config;

public record SigtranConfig(
        String localIp,
        int localPort,
        String remoteIp,
        int remotePort,
        int localPointCode,
        int remotePointCode,
        int localSsn,
        int remoteSsn,
        long routingContext,
        String associationName,
        String stackName
) {

    public SigtranConfig {
        if (localIp == null || localIp.isBlank()) {
            throw new IllegalArgumentException("Local IP is required");
        }

        if (remoteIp == null || remoteIp.isBlank()) {
            throw new IllegalArgumentException("Remote IP is required");
        }

        if (localPort < 1 || localPort > 65535
                || remotePort < 1 || remotePort > 65535) {
            throw new IllegalArgumentException("Invalid SCTP port");
        }

        if (localPointCode < 0 || remotePointCode < 0) {
            throw new IllegalArgumentException("Point Code cannot be negative");
        }

        if (localSsn < 0 || localSsn > 255
                || remoteSsn < 0 || remoteSsn > 255) {
            throw new IllegalArgumentException("Invalid SSN");
        }

        if (routingContext < 0 || routingContext > 0xFFFFFFFFL) {
            throw new IllegalArgumentException("Invalid Routing Context");
        }

        if (associationName == null || associationName.isBlank()) {
            throw new IllegalArgumentException("Association name is required");
        }

        if (stackName == null || stackName.isBlank()) {
            throw new IllegalArgumentException("Stack name is required");
        }
    }

    public static SigtranConfig localStub() {
        return new SigtranConfig(
                "127.0.0.1",
                2906,
                "127.0.0.1",
                2905,
                1,
                2,
                8,
                8,
                100,
                "generatorAssociation",
                "CamelGenerator"
        );
    }
}
