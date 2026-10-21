
package org.example.camel.generator;

import org.example.camel.config.SigtranConfig;
import org.mobicents.protocols.api.Association;
import org.mobicents.protocols.api.IpChannelType;
import org.mobicents.protocols.sctp.ManagementImpl;

import java.io.IOException;
import java.util.Objects;

public class SctpClient {

    private final SigtranConfig config;
    private final ManagementImpl sctpManagement;

    public SctpClient(SigtranConfig config) throws IOException {
        this.config = Objects.requireNonNull(config);
        this.sctpManagement = new ManagementImpl(
                config.stackName() + "Sctp"
        );
    }

    public void start() throws Exception {

        sctpManagement.start();

        Association association = sctpManagement.getAssociation(
                config.associationName()
        );

        if (association == null) {

            sctpManagement.addAssociation(
                    config.localIp(),
                    config.localPort(),
                    config.remoteIp(),
                    config.remotePort(),
                    config.associationName(),
                    IpChannelType.SCTP,
                    null
            );

        } else {

            validateAssociation(association);
        }

        association = sctpManagement.getAssociation(
                config.associationName()
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
                        && association.getIpChannelType() == IpChannelType.SCTP;

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
}
