package org.example.camel.generator;

import org.example.camel.listener.CapClientListener;
import org.restcomm.protocols.ss7.cap.CAPStackImpl;
import org.restcomm.protocols.ss7.cap.api.CAPProvider;
import org.restcomm.protocols.ss7.cap.api.service.circuitSwitchedCall.CAPServiceCircuitSwitchedCall;
import org.restcomm.protocols.ss7.sccp.SccpProvider;
import org.restcomm.protocols.ss7.tcap.api.TCAPProvider;

import org.restcomm.protocols.ss7.indicator.RoutingIndicator;
import org.restcomm.protocols.ss7.sccp.parameter.SccpAddress;
import org.restcomm.protocols.ss7.cap.api.CAPApplicationContext;
import org.restcomm.protocols.ss7.cap.api.service.circuitSwitchedCall.CAPDialogCircuitSwitchedCall;
import org.example.camel.config.SigtranConfig;

import java.util.Objects;
import java.util.concurrent.CompletableFuture;

public class CapClient {

    private static final String STACK_NAME = "CamelGeneratorCap";

    private final CAPStackImpl capStack;
    private final CapClientListener listener;

    private final SccpProvider sccpProvider;
    private final SigtranConfig config;
    private final CapRequestTracker requestTracker =
            new CapRequestTracker();

    public CapClient(
            TCAPProvider tcapProvider,
            SccpProvider sccpProvider,
            SigtranConfig config
    ) {
        this.config = Objects.requireNonNull(config);
        this.sccpProvider = sccpProvider;

        this.capStack = new CAPStackImpl(
                config.stackName() + "Cap",
                tcapProvider
        );

        this.listener = new CapClientListener(requestTracker);
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

    public CapRequest sendInitialDP(int serviceKey) throws Exception {

        CAPProvider provider = capStack.getCAPProvider();

        SccpAddress localAddress =
                sccpProvider.getParameterFactory().createSccpAddress(
                        RoutingIndicator.ROUTING_BASED_ON_DPC_AND_SSN,
                        null,
                        config.localPointCode(),
                        config.localSsn()
                );

        SccpAddress remoteAddress =
                sccpProvider.getParameterFactory().createSccpAddress(
                        RoutingIndicator.ROUTING_BASED_ON_DPC_AND_SSN,
                        null,
                        config.remotePointCode(),
                        config.remoteSsn()
                );

        CAPDialogCircuitSwitchedCall dialog =
                provider.getCAPServiceCircuitSwitchedCall()
                        .createNewDialog(
                                CAPApplicationContext.CapV2_gsmSSF_to_gsmSCF,
                                localAddress,
                                remoteAddress
                        );

        dialog.addInitialDPRequest(
                serviceKey,       // serviceKey
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

        System.out.println(
                "CAP InitialDP sent. Dialog ID=" +
                        dialog.getLocalDialogId()
        );

        long dialogId = dialog.getLocalDialogId();

// Регистрируем запрос до отправки
        CompletableFuture<Void> future =
                requestTracker.register(dialogId);

        try {
            // Отправляем InitialDP только один раз
            dialog.send();

            System.out.println(
                    "CAP InitialDP sent. Dialog ID=" + dialogId
            );

            return new CapRequest(dialogId, future);

        } catch (Exception e) {
            requestTracker.fail(dialogId, e);
            throw e;
        }
    }

    public void cancelRequest(long dialogId) {
        requestTracker.cancel(dialogId);
    }

}