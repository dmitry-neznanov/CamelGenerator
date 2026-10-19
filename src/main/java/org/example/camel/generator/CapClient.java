package org.example.camel.generator;

import org.example.camel.listener.CapClientListener;
import org.restcomm.protocols.ss7.cap.CAPStackImpl;
import org.restcomm.protocols.ss7.cap.api.CAPProvider;
import org.restcomm.protocols.ss7.cap.api.service.circuitSwitchedCall.CAPServiceCircuitSwitchedCall;
import org.restcomm.protocols.ss7.sccp.SccpProvider;
import org.restcomm.protocols.ss7.tcap.api.TCAPProvider;

import org.restcomm.protocols.ss7.indicator.RoutingIndicator;
import org.restcomm.protocols.ss7.sccp.SccpProvider;
import org.restcomm.protocols.ss7.sccp.parameter.SccpAddress;
import org.restcomm.protocols.ss7.cap.api.CAPApplicationContext;
import org.restcomm.protocols.ss7.cap.api.service.circuitSwitchedCall.CAPDialogCircuitSwitchedCall;

public class CapClient {

    private static final String STACK_NAME = "CamelGeneratorCap";

    private final CAPStackImpl capStack;
    private final CapClientListener listener;

    private final SccpProvider sccpProvider;

    public CapClient(
            TCAPProvider tcapProvider,
            SccpProvider sccpProvider
    ) {
        this.sccpProvider = sccpProvider;

        this.capStack = new CAPStackImpl(
                STACK_NAME,
                tcapProvider
        );

        this.listener = new CapClientListener();
    }

    public void start() throws Exception {
        System.out.println("Starting CAP client...");

        capStack.start();

        CAPProvider provider = capStack.getCAPProvider();

        CAPServiceCircuitSwitchedCall service =
                provider.getCAPServiceCircuitSwitchedCall();

        service.addCAPServiceListener(listener);
        provider.addCAPDialogListener(listener);

        service.acivate();

        System.out.println("CAP client started");
    }

    public void stop() {
        capStack.stop();

        System.out.println("CAP client stopped");
    }

    public CAPProvider getProvider() {
        return capStack.getCAPProvider();
    }

    public long sendInitialDP() throws Exception {

        CAPProvider provider = capStack.getCAPProvider();

        SccpAddress localAddress =
                sccpProvider.getParameterFactory().createSccpAddress(
                        RoutingIndicator.ROUTING_BASED_ON_DPC_AND_SSN,
                        null,
                        1,
                        8
                );

        SccpAddress remoteAddress =
                sccpProvider.getParameterFactory().createSccpAddress(
                        RoutingIndicator.ROUTING_BASED_ON_DPC_AND_SSN,
                        null,
                        2,
                        8
                );

        CAPDialogCircuitSwitchedCall dialog =
                provider.getCAPServiceCircuitSwitchedCall()
                        .createNewDialog(
                                CAPApplicationContext.CapV2_gsmSSF_to_gsmSCF,
                                localAddress,
                                remoteAddress
                        );

        dialog.addInitialDPRequest(
                1,       // serviceKey
                null,    // calledPartyNumber
                null,    // callingPartyNumber
                null,    // callingPartysCategory
                null,    // cGEncountered
                null,    // iPSSPCapabilities
                null,    // locationNumber
                null,    // originalCalledPartyID
                null,    // extensions
                null,    // highLayerCompatibility
                null,    // additionalCallingPartyNumber
                null,    // bearerCapability
                null,    // eventTypeBCSM
                null,    // redirectingPartyID
                null,    // redirectionInformation
                null,    // cause
                null,    // serviceInteractionIndicatorsTwo
                null,    // carrier
                null,    // cugIndex
                null,    // cugInterlock
                false,   // cugOutgoingAccess
                null,    // imsi
                null,    // subscriberState
                null,    // locationInformation
                null,    // extBasicServiceCode
                null,    // callReferenceNumber
                null,    // mscAddress
                null,    // calledPartyBCDNumber
                null,    // timeAndTimezone
                false,   // callForwardingSSPending
                null     // initialDPArgExtension
        );

        dialog.send();

        System.out.println(
                "CAP InitialDP sent. Dialog ID=" +
                        dialog.getLocalDialogId()
        );

        return dialog.getLocalDialogId();
    }
}