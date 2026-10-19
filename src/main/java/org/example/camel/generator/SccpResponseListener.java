package org.example.camel.generator;

import org.restcomm.protocols.ss7.sccp.*;
import org.restcomm.protocols.ss7.sccp.message.*;
import org.restcomm.protocols.ss7.sccp.parameter.*;

import java.nio.charset.StandardCharsets;

public class SccpResponseListener implements SccpListener {

    @Override
    public void onMessage(SccpDataMessage message) {

        String response = new String(
                message.getData(),
                StandardCharsets.UTF_8
        );

        System.out.println("SCCP response received: " + response);
    }

    @Override
    public void onNotice(SccpNoticeMessage message) {
        System.out.println("SCCP notice received: " + message);
    }

    @Override
    public void onCoordResponse(int dpc, int ssn) {
    }

    @Override
    public void onState(int dpc, int ssn, boolean inService, int multiplicityIndicator) {
    }

    @Override
    public void onPcState(
            int dpc,
            SignallingPointStatus status,
            Integer restrictedImportanceLevel,
            RemoteSccpStatus remoteSccpStatus) {
    }

    @Override
    public void onNetworkIdState(int networkId, NetworkIdState state) {
    }

    @Override
    public void onConnectIndication(
            SccpConnection connection,
            SccpAddress calledParty,
            SccpAddress callingParty,
            ProtocolClass protocolClass,
            Credit credit,
            byte[] data,
            Importance importance) throws Exception {
    }

    @Override
    public void onConnectConfirm(SccpConnection connection, byte[] data) {
    }

    @Override
    public void onDisconnectIndication(
            SccpConnection connection,
            ReleaseCause cause,
            byte[] data) {
    }

    @Override
    public void onDisconnectIndication(
            SccpConnection connection,
            RefusalCause cause,
            byte[] data) {
    }

    @Override
    public void onDisconnectIndication(
            SccpConnection connection,
            ErrorCause cause) {
    }

    @Override
    public void onResetIndication(
            SccpConnection connection,
            ResetCause cause) {
    }

    @Override
    public void onResetConfirm(SccpConnection connection) {
    }

    @Override
    public void onData(SccpConnection connection, byte[] data) {
    }

    @Override
    public void onDisconnectConfirm(SccpConnection connection) {
    }
}

