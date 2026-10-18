package org.example.camel.generator;

import org.example.camel.generator.SctpAssociationListener;
import org.mobicents.protocols.sctp.ManagementImpl;

import java.io.IOException;

public class SctpClient {

    private static final String LOCAL_IP = "127.0.0.1";
    private static final int LOCAL_PORT = 2906;

    private static final String REMOTE_IP = "127.0.0.1";
    private static final int REMOTE_PORT = 2905;

    private static final String ASSOCIATION_NAME = "generatorAssociation";
    private static final String MANAGEMENT_NAME = "CamelGeneratorSctp";

    private final ManagementImpl sctpManagement;

    public SctpClient() throws IOException {
        sctpManagement = new ManagementImpl(MANAGEMENT_NAME);
    }

    public void start() throws Exception {

        sctpManagement.start();

        if (sctpManagement.getAssociation(ASSOCIATION_NAME) == null) {

            sctpManagement.addAssociation(
                    LOCAL_IP,
                    LOCAL_PORT,
                    REMOTE_IP,
                    REMOTE_PORT,
                    ASSOCIATION_NAME
            );
        }

        sctpManagement.getAssociation(ASSOCIATION_NAME)
                .setAssociationListener(new SctpAssociationListener());

        sctpManagement.startAssociation(ASSOCIATION_NAME);
    }

    public void stop() throws Exception {
        sctpManagement.stop();
    }
}