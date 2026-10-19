package org.example.camel.generator;

import org.restcomm.protocols.ss7.indicator.RoutingIndicator;
import org.restcomm.protocols.ss7.sccp.SccpProvider;
import org.restcomm.protocols.ss7.sccp.parameter.SccpAddress;
import org.restcomm.protocols.ss7.tcap.TCAPStackImpl;
import org.restcomm.protocols.ss7.tcap.api.TCAPProvider;
import org.restcomm.protocols.ss7.tcap.api.tc.dialog.Dialog;
import org.restcomm.protocols.ss7.tcap.api.tc.dialog.events.TCBeginRequest;

public class TcapClient {

    private static final String STACK_NAME = "CamelGeneratorTcap";
    private static final int LOCAL_SSN = 8;

    private final TCAPStackImpl tcapStack;
    private final SccpProvider sccpProvider;

    private final TcapClientListener listener = new TcapClientListener();

    public TcapClient(SccpProvider sccpProvider) {

        this.sccpProvider = sccpProvider;

        this.tcapStack = new TCAPStackImpl(
                "CamelGeneratorTcap",
                sccpProvider,
                8
        );
    }

    public void start() throws Exception {

        System.out.println("Starting TCAP client...");

        tcapStack.start();
        tcapStack.getProvider().addTCListener(listener);

        System.out.println("TCAP client started");
    }

    public void stop() {
        tcapStack.getProvider().removeTCListener(listener);
        tcapStack.stop();

        System.out.println("TCAP client stopped");
    }

    public TCAPStackImpl getStack() {
        return tcapStack;
    }


    public void sendBegin() throws Exception {

        TCAPProvider provider = tcapStack.getProvider();

        // Адрес CamelGenerator
        SccpAddress localAddress =
                sccpProvider.getParameterFactory().createSccpAddress(
                        RoutingIndicator.ROUTING_BASED_ON_DPC_AND_SSN,
                        null,
                        1,
                        8
                );

        // Адрес CamelStub
        SccpAddress remoteAddress =
                sccpProvider.getParameterFactory().createSccpAddress(
                        RoutingIndicator.ROUTING_BASED_ON_DPC_AND_SSN,
                        null,
                        2,
                        8
                );

        // Создаём TCAP-диалог
        Dialog dialog = provider.getNewDialog(
                localAddress,
                remoteAddress
        );

        // Создаём TC-BEGIN
        TCBeginRequest beginRequest =
                provider.getDialogPrimitiveFactory().createBegin(dialog);

        // Отправляем TC-BEGIN
        dialog.send(beginRequest);

        System.out.println(
                "TCAP BEGIN sent. Dialog ID=" + dialog.getLocalDialogId()
        );
    }

}
