package org.example.camel.generator;

import org.mobicents.protocols.api.Association;
import org.mobicents.protocols.api.IpChannelType;
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
                    ASSOCIATION_NAME,
                    IpChannelType.SCTP,
                    null
            );
        }


        Association association =
                sctpManagement.getAssociation(ASSOCIATION_NAME);

        System.out.println("Association type: " + association.getIpChannelType());
        System.out.println("Association host: " + association.getHostAddress());
        System.out.println("Association host port: " + association.getHostPort());
        System.out.println("Association peer: " + association.getPeerAddress());
        System.out.println("Association peer port: " + association.getPeerPort());


    }

    public void stop() throws Exception {
        sctpManagement.stop();
    }

    public ManagementImpl getManagement() {
        return sctpManagement;
    }

    public String getAssociationName() {
        return ASSOCIATION_NAME;
    }
}