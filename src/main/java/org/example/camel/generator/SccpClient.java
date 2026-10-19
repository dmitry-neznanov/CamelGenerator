
package org.example.camel.generator;

import org.restcomm.protocols.ss7.m3ua.impl.M3UAManagementImpl;
import org.restcomm.protocols.ss7.sccp.impl.SccpStackImpl;
import org.restcomm.protocols.ss7.sccp.Mtp3ServiceAccessPoint;

import java.util.Objects;

public class SccpClient {

    private static final String STACK_NAME = "CamelGeneratorSccp";

    private static final int SAP_ID = 1;
    private static final int MTP3_ID = 1;
    private static final int LOCAL_PC = 1;
    private static final int NETWORK_INDICATOR = 2;
    private static final int NETWORK_ID = 0;

    private static final int DESTINATION_ID = 1;
    private static final int REMOTE_PC = 2;

    private static final int FIRST_SLS = 0;
    private static final int LAST_SLS = 255;
    private static final int SLS_MASK = 255;

    private static final int REMOTE_SPC_ID = 1;
    private static final int REMOTE_SSN_ID = 1;
    private static final int REMOTE_SSN = 8;

    private static final String LOCAL_GT_DIGITS = "";

    private final SccpStackImpl sccpStack;

    public SccpClient(M3UAManagementImpl m3uaManagement) {
        sccpStack = new SccpStackImpl(STACK_NAME, null);
        sccpStack.setMtp3UserPart(MTP3_ID, m3uaManagement);
    }

    public void start() throws Exception {

        System.out.println("Starting SCCP client...");

        sccpStack.start();

        configureSap();
        configureDestination();
        configureRemoteSpc();
        configureRemoteSsn();

        System.out.println("SCCP client configured");
    }

    private void configureSap() throws Exception {

        var router = sccpStack.getRouter();

        Mtp3ServiceAccessPoint sap =
                router.getMtp3ServiceAccessPoint(SAP_ID);

        if (sap == null) {

            router.addMtp3ServiceAccessPoint(
                    SAP_ID,
                    MTP3_ID,
                    LOCAL_PC,
                    NETWORK_INDICATOR,
                    NETWORK_ID,
                    LOCAL_GT_DIGITS
            );

            System.out.println("SCCP SAP created");
            return;
        }

        boolean configurationMatches =
                sap.getMtp3Id() == MTP3_ID
                        && sap.getOpc() == LOCAL_PC
                        && sap.getNi() == NETWORK_INDICATOR
                        && sap.getNetworkId() == NETWORK_ID
                        && Objects.equals(
                        sap.getLocalGtDigits(),
                        LOCAL_GT_DIGITS
                );

        if (configurationMatches) {
            System.out.println("SCCP SAP already configured");
            return;
        }

        router.modifyMtp3ServiceAccessPoint(
                SAP_ID,
                MTP3_ID,
                LOCAL_PC,
                NETWORK_INDICATOR,
                NETWORK_ID,
                LOCAL_GT_DIGITS
        );

        System.out.println("SCCP SAP configuration updated");
    }

    public void stop() {
        sccpStack.stop();
    }

    public SccpStackImpl getStack() {
        return sccpStack;
    }

    private void configureDestination() throws Exception {

        var router = sccpStack.getRouter();

        var sap = router.getMtp3ServiceAccessPoint(SAP_ID);

        if (sap == null) {
            throw new IllegalStateException(
                    "MTP3 SAP not found: " + SAP_ID
            );
        }

        var destination = sap.getMtp3Destination(DESTINATION_ID);

        if (destination == null) {

            router.addMtp3Destination(
                    SAP_ID,
                    DESTINATION_ID,
                    REMOTE_PC,
                    REMOTE_PC,
                    FIRST_SLS,
                    LAST_SLS,
                    SLS_MASK
            );

            System.out.println("MTP3 Destination created");
            return;
        }

        boolean configurationMatches =
                destination.getFirstDpc() == REMOTE_PC
                        && destination.getLastDpc() == REMOTE_PC
                        && destination.getFirstSls() == FIRST_SLS
                        && destination.getLastSls() == LAST_SLS
                        && destination.getSlsMask() == SLS_MASK;

        if (configurationMatches) {
            System.out.println("MTP3 Destination already configured");
            return;
        }

        router.modifyMtp3Destination(
                SAP_ID,
                DESTINATION_ID,
                REMOTE_PC,
                REMOTE_PC,
                FIRST_SLS,
                LAST_SLS,
                SLS_MASK
        );

        System.out.println("MTP3 Destination configuration updated");
    }

    private void configureRemoteSpc() throws Exception {

        var resource = sccpStack.getSccpResource();

        var remoteSpc = resource.getRemoteSpc(REMOTE_SPC_ID);

        if (remoteSpc == null) {

            resource.addRemoteSpc(
                    REMOTE_SPC_ID,
                    REMOTE_PC,
                    0,
                    0
            );

            System.out.println("Remote SPC created");
            return;
        }

        boolean configurationMatches =
                remoteSpc.getRemoteSpc() == REMOTE_PC
                        && remoteSpc.getRemoteSpcFlag() == 0
                        && remoteSpc.getMask() == 0;

        if (configurationMatches) {
            System.out.println("Remote SPC already configured");
            return;
        }

        resource.modifyRemoteSpc(
                REMOTE_SPC_ID,
                REMOTE_PC,
                0,
                0
        );

        System.out.println("Remote SPC configuration updated");
    }

    private void configureRemoteSsn() throws Exception {

        var resource = sccpStack.getSccpResource();

        var remoteSsn = resource.getRemoteSsn(REMOTE_SSN_ID);

        if (remoteSsn == null) {

            resource.addRemoteSsn(
                    REMOTE_SSN_ID,
                    REMOTE_PC,
                    REMOTE_SSN,
                    0,
                    false
            );

            System.out.println("Remote SSN created");
            return;
        }

        boolean configurationMatches =
                remoteSsn.getRemoteSpc() == REMOTE_PC
                        && remoteSsn.getRemoteSsn() == REMOTE_SSN
                        && remoteSsn.getRemoteSsnFlag() == 0
                        && !remoteSsn.getMarkProhibitedWhenSpcResuming();

        if (configurationMatches) {
            System.out.println("Remote SSN already configured");
            return;
        }

        resource.modifyRemoteSsn(
                REMOTE_SSN_ID,
                REMOTE_PC,
                REMOTE_SSN,
                0,
                false
        );

        System.out.println("Remote SSN configuration updated");
    }
}
