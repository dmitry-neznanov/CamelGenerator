package org.example.camel.listener;

import org.mobicents.protocols.api.Association;
import org.mobicents.protocols.api.AssociationListener;
import org.mobicents.protocols.api.PayloadData;

public class SctpAssociationListener implements AssociationListener {

    @Override
    public void onCommunicationUp(
            Association association,
            int maxInboundStreams,
            int maxOutboundStreams) {

        System.out.println(
                "SCTP UP: " + association.getName()
        );
    }

    @Override
    public void onCommunicationShutdown(Association association) {
        System.out.println(
                "SCTP SHUTDOWN: " + association.getName()
        );
    }

    @Override
    public void onCommunicationLost(Association association) {
        System.out.println(
                "SCTP LOST: " + association.getName()
        );
    }

    @Override
    public void onCommunicationRestart(Association association) {
        System.out.println(
                "SCTP RESTART: " + association.getName()
        );
    }

    @Override
    public void onPayload(Association association, PayloadData payloadData) {
        System.out.println(
                "SCTP PAYLOAD RECEIVED: " + association.getName()
        );
    }

    @Override
    public void inValidStreamId(PayloadData payloadData) {
        System.out.println("Invalid SCTP stream ID");
    }
}