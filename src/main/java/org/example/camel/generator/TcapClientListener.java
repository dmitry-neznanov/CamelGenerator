package org.example.camel.generator;

import org.restcomm.protocols.ss7.tcap.api.TCListener;
import org.restcomm.protocols.ss7.tcap.api.tc.dialog.Dialog;
import org.restcomm.protocols.ss7.tcap.api.tc.dialog.events.*;
import org.restcomm.protocols.ss7.tcap.asn.comp.Invoke;

public class TcapClientListener implements TCListener {

    @Override
    public void onTCUni(TCUniIndication indication) {
        System.out.println("TCAP UNI received");
    }

    @Override
    public void onTCBegin(TCBeginIndication indication) {
        System.out.println("TCAP BEGIN received");
    }

    @Override
    public void onTCContinue(TCContinueIndication indication) {
        System.out.println("TCAP CONTINUE received");
    }

    @Override
    public void onTCEnd(TCEndIndication indication) {

        Dialog dialog = indication.getDialog();

        System.out.println(
                "TCAP END received. Dialog ID="
                        + dialog.getLocalDialogId()
        );
    }

    @Override
    public void onTCUserAbort(TCUserAbortIndication indication) {
        System.out.println("TCAP USER ABORT received");
    }

    @Override
    public void onTCPAbort(TCPAbortIndication indication) {
        System.out.println("TCAP PROVIDER ABORT received");
    }

    @Override
    public void onTCNotice(TCNoticeIndication indication) {
        System.out.println("TCAP NOTICE received");
    }

    @Override
    public void onDialogReleased(Dialog dialog) {

        System.out.println(
                "TCAP dialog released. Dialog ID="
                        + dialog.getLocalDialogId()
        );
    }

    @Override
    public void onInvokeTimeout(Invoke invoke) {
        System.out.println("TCAP invoke timeout");
    }

    @Override
    public void onDialogTimeout(Dialog dialog) {
        System.out.println("TCAP dialog timeout");
    }
}
