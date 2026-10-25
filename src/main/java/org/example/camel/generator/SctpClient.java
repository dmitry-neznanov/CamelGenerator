
package org.example.camel.generator;

import org.example.camel.config.SigtranConfig;
import org.mobicents.protocols.api.Association;
import org.mobicents.protocols.api.IpChannelType;
import org.mobicents.protocols.sctp.ManagementImpl;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;

public class SctpClient {

    private static final String CONFIG_DIR_PROPERTY =
            "camel.sigtran.config.dir";

    private static final String DEFAULT_CONFIG_DIR =
            "camel-sigtran/generator";

    private static final IpChannelType CHANNEL_TYPE =
            IpChannelType.SCTP;

    private final SigtranConfig config;
    private final ManagementImpl sctpManagement;
    private final Path persistDir;

    public SctpClient(SigtranConfig config) throws IOException {

        this.config = Objects.requireNonNull(config);

        this.sctpManagement = new ManagementImpl(
                config.stackName() + "Sctp"
        );

        String configuredDir = System.getProperty(
                CONFIG_DIR_PROPERTY
        );

        if (configuredDir == null || configuredDir.isBlank()) {
            persistDir = Path.of(
                    System.getProperty("user.home"),
                    DEFAULT_CONFIG_DIR
            ).toAbsolutePath().normalize();
        } else {
            persistDir = Path.of(configuredDir)
                    .toAbsolutePath()
                    .normalize();
        }

        Files.createDirectories(persistDir);

        sctpManagement.setPersistDir(persistDir.toString());
    }

    public void start() throws Exception {

        sctpManagement.start();

        Association association = sctpManagement
                .getAssociations()
                .get(config.associationName());

        if (association == null) {

            association = sctpManagement.addAssociation(
                    config.localIp(),
                    config.localPort(),
                    config.remoteIp(),
                    config.remotePort(),
                    config.associationName(),
                    CHANNEL_TYPE,
                    null
            );

            System.out.println(
                    "SCTP association created: "
                            + config.associationName()
            );

        } else {

            validateAssociation(association);

            System.out.println(
                    "SCTP association loaded: "
                            + config.associationName()
            );
        }

        System.out.println(
                "SCTP config directory: " + persistDir
        );

        System.out.println(
                "SCTP association: " + config.associationName()
        );

        System.out.println(
                "Local: " + association.getHostAddress()
                        + ":" + association.getHostPort()
        );

        System.out.println(
                "Remote: " + association.getPeerAddress()
                        + ":" + association.getPeerPort()
        );

        System.out.println(
                "SCTP association started: "
                        + association.isStarted()
        );
    }

    private void validateAssociation(Association association) {

        boolean matches =
                Objects.equals(
                        association.getHostAddress(),
                        config.localIp()
                )
                        && association.getHostPort() == config.localPort()
                        && Objects.equals(
                        association.getPeerAddress(),
                        config.remoteIp()
                )
                        && association.getPeerPort() == config.remotePort()
                        && association.getIpChannelType() == CHANNEL_TYPE;

        if (!matches) {
            throw new IllegalStateException(
                    "Existing SCTP association does not match "
                            + "the supplied configuration: "
                            + config.associationName()
                            + ". Check persisted jSS7 configuration."
            );
        }
    }

    public void stop() throws Exception {
        sctpManagement.stop();
    }

    public ManagementImpl getManagement() {
        return sctpManagement;
    }

    public String getAssociationName() {
        return config.associationName();
    }

    public Path getPersistDir() {
        return persistDir;
    }
}
