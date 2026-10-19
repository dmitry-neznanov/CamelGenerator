package org.example.camel.generator;

import org.restcomm.protocols.ss7.sccp.SccpProvider;
import org.restcomm.protocols.ss7.tcap.TCAPStackImpl;

public class TcapClient {

    private static final String STACK_NAME = "CamelGeneratorTcap";
    private static final int LOCAL_SSN = 8;

    private final TCAPStackImpl tcapStack;

    public TcapClient(SccpProvider sccpProvider) {

        this.tcapStack = new TCAPStackImpl(
                STACK_NAME,
                sccpProvider,
                LOCAL_SSN
        );
    }

    public TCAPStackImpl getStack() {
        return tcapStack;
    }
}