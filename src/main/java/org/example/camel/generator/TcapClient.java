package org.example.camel.generator;

import org.restcomm.protocols.ss7.sccp.SccpProvider;
import org.restcomm.protocols.ss7.tcap.TCAPStackImpl;

public class TcapClient {

    private static final String STACK_NAME = "CamelGeneratorTcap";
    private static final int LOCAL_SSN = 8;

    private final TCAPStackImpl tcapStack;

    public TcapClient(SccpProvider sccpProvider) {

        tcapStack = new TCAPStackImpl(
                STACK_NAME,
                sccpProvider,
                LOCAL_SSN
        );
    }

    public void start() throws Exception {

        System.out.println("Starting TCAP client...");

        tcapStack.start();

        System.out.println("TCAP client started");
    }

    public void stop() {

        tcapStack.stop();

        System.out.println("TCAP client stopped");
    }

    public TCAPStackImpl getStack() {
        return tcapStack;
    }
}
