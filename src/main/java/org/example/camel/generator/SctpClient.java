
package org.example.camel.generator;

import org.mobicents.protocols.sctp.ManagementImpl;

import java.io.IOException;

public class SctpClient {

    private static final String ASSOCIATION_NAME = "generatorAssociation";

    private final ManagementImpl sctpManagement;

    public SctpClient() throws IOException {
        sctpManagement = new ManagementImpl("CamelGeneratorSctp");
    }

    public void start() throws Exception {

        sctpManagement.start();

        sctpManagement.addAssociation(
                "127.0.0.1",
                2906,
                "127.0.0.1",
                2905,
                ASSOCIATION_NAME
        );

        sctpManagement.startAssociation(ASSOCIATION_NAME);

        System.out.println(
                "SCTP client started. Connecting to 127.0.0.1:2905"
        );
    }

    public void stop() throws Exception {
        sctpManagement.stop();
    }
}
