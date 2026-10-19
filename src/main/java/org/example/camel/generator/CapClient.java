package org.example.camel.generator;

import org.example.camel.listener.CapClientListener;
import org.restcomm.protocols.ss7.cap.CAPStackImpl;
import org.restcomm.protocols.ss7.cap.api.CAPProvider;
import org.restcomm.protocols.ss7.cap.api.service.circuitSwitchedCall.CAPServiceCircuitSwitchedCall;
import org.restcomm.protocols.ss7.tcap.api.TCAPProvider;

public class CapClient {

    private static final String STACK_NAME = "CamelGeneratorCap";

    private final CAPStackImpl capStack;
    private final CapClientListener listener;

    public CapClient(TCAPProvider tcapProvider) {
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
}