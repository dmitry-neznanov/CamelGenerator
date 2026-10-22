
package org.example.camel.generator;

import org.mobicents.protocols.sctp.ManagementImpl;
import org.restcomm.protocols.ss7.m3ua.ExchangeType;
import org.restcomm.protocols.ss7.m3ua.Functionality;
import org.restcomm.protocols.ss7.m3ua.impl.M3UAManagementImpl;
import org.restcomm.protocols.ss7.m3ua.impl.parameter.ParameterFactoryImpl;
import org.restcomm.protocols.ss7.m3ua.parameter.ParameterFactory;
import org.restcomm.protocols.ss7.m3ua.parameter.RoutingContext;
import org.restcomm.protocols.ss7.m3ua.parameter.TrafficModeType;
import org.example.camel.config.SigtranConfig;
import java.util.Objects;
import java.util.Arrays;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.restcomm.protocols.ss7.m3ua.As;
import org.restcomm.protocols.ss7.m3ua.Asp;
import org.restcomm.protocols.ss7.m3ua.AspFactory;

public class M3uaClient {

    private final SigtranConfig config;
    private final String asName;
    private final String aspName;

    private static final int MIN_ASP_ACTIVE = 1;

    private static final int WILDCARD_OPC = -1;
    private static final int WILDCARD_SI = -1;

    private static final Functionality FUNCTIONALITY = Functionality.AS;
    private static final ExchangeType EXCHANGE_TYPE = ExchangeType.SE;
    private static final int TRAFFIC_MODE = TrafficModeType.Loadshare;

    private final M3UAManagementImpl m3uaManagement;
    private final String associationName;
    private final ManagementImpl sctpManagement;

    public M3uaClient(
            ManagementImpl sctpManagement,
            String associationName,
            SigtranConfig config
    ) {
        this.config = Objects.requireNonNull(config);

        this.asName = config.stackName() + "AS";
        this.aspName = config.stackName() + "ASP";

        this.associationName = associationName;
        this.sctpManagement = sctpManagement;

        m3uaManagement = new M3UAManagementImpl(
                config.stackName() + "M3ua",
                config.stackName(),
                null
        );

        m3uaManagement.setTransportManagement(sctpManagement);
    }


    public void start() throws Exception {

        System.out.println(
                "Before M3UA start, SCTP started: "
                        + sctpManagement.getAssociation(associationName).isStarted()
        );

        m3uaManagement.start();

        System.out.println(
                "After M3UA start, SCTP started: "
                        + sctpManagement.getAssociation(associationName).isStarted()
        );

        System.out.println("=== EXISTING ASP FACTORIES ===");

        m3uaManagement.getAspfactories().forEach(asp -> {
            System.out.println(
                    "ASP Factory: " + asp.getName()
                            + ", started: " + asp.getStatus()
                            + ", association: "
                            + (asp.getAssociation() == null
                            ? "null"
                            : asp.getAssociation().getName())
            );
        });

        configureAs();
        configureAsp();

        if (m3uaManagement.getRoute().isEmpty()) {
            m3uaManagement.addRoute(
                    config.remotePointCode(),
                    WILDCARD_OPC,
                    WILDCARD_SI,
                    asName
            );
        }

        System.out.println("=== M3UA ROUTES ===");

        m3uaManagement.getRoute().forEach((key, route) -> {

            System.out.println("Route key: " + key);

            for (As as : route.getAsArray()) {

                if (as != null) {
                    System.out.println("  AS: " + as.getName());
                }
            }
        });

        System.out.println("M3UA client configured");
    }


    public void startAsp() throws Exception {

        boolean started = false;

        for (var asp : m3uaManagement.getAspfactories()) {
            if (aspName.equals(asp.getName())) {
                started = asp.getStatus();
                break;
            }
        }

        if (!started) {
            m3uaManagement.startAsp(aspName);
            System.out.println("M3UA client ASP started");
        } else {
            System.out.println("M3UA client ASP already started");
        }
    }

    public M3UAManagementImpl getManagement() {
        return m3uaManagement;
    }

    public void stop() throws Exception {
        m3uaManagement.stop();
    }

    private void configureAs() throws Exception {

        ParameterFactory factory = new ParameterFactoryImpl();

        RoutingContext routingContext = factory.createRoutingContext(
                new long[]{config.routingContext()}
        );

        TrafficModeType trafficMode =
                factory.createTrafficModeType(TRAFFIC_MODE);

        As existingAs = null;

        for (As as : m3uaManagement.getAppServers()) {
            if (asName.equals(as.getName())) {
                existingAs = as;
                break;
            }
        }

        if (existingAs == null) {

            m3uaManagement.createAs(
                    asName,
                    FUNCTIONALITY,
                    EXCHANGE_TYPE,
                    null,
                    routingContext,
                    trafficMode,
                    MIN_ASP_ACTIVE,
                    null
            );

            System.out.println("M3UA AS created: " + asName);
            return;
        }

        boolean configurationMatches =
                existingAs.getFunctionality() == FUNCTIONALITY
                        && existingAs.getExchangeType() == EXCHANGE_TYPE
                        && existingAs.getIpspType() == null
                        && existingAs.getMinAspActiveForLb() == MIN_ASP_ACTIVE
                        && existingAs.getNetworkAppearance() == null
                        && existingAs.getRoutingContext() != null
                        && Arrays.equals(
                        existingAs.getRoutingContext().getRoutingContexts(),
                        new long[]{config.routingContext()}
                )
                        && existingAs.getTrafficModeType() != null
                        && existingAs.getTrafficModeType().getMode() == TRAFFIC_MODE;

        if (!configurationMatches) {
            throw new IllegalStateException(
                    "Existing M3UA AS configuration mismatch: " + asName
                            + ". Check Routing Context, Traffic Mode and AS settings."
            );
        }

        System.out.println("M3UA AS configuration matches: " + asName);
    }


    private void configureAsp() throws Exception {

        AspFactory existingFactory = null;

        for (AspFactory asp : m3uaManagement.getAspfactories()) {
            if (aspName.equals(asp.getName())) {
                existingFactory = asp;
                break;
            }
        }

        if (existingFactory == null) {

            var association = sctpManagement.getAssociation(associationName);

            if (association.isStarted()) {
                throw new IllegalStateException(
                        "Cannot create ASP Factory because SCTP association "
                                + associationName + " is already started"
                );
            }

            m3uaManagement.createAspFactory(
                    aspName,
                    associationName
            );

            System.out.println("M3UA ASP Factory created: " + aspName);

        } else {

            if (existingFactory.getAssociation() == null) {
                throw new IllegalStateException(
                        "ASP Factory has no SCTP association: " + aspName
                );
            }

            String actualAssociation =
                    existingFactory.getAssociation().getName();

            if (!associationName.equals(actualAssociation)) {
                throw new IllegalStateException(
                        "ASP association mismatch. Expected: "
                                + associationName
                                + ", actual: "
                                + actualAssociation
                );
            }
        }

        As targetAs = null;

        for (As as : m3uaManagement.getAppServers()) {
            if (asName.equals(as.getName())) {
                targetAs = as;
                break;
            }
        }

        if (targetAs == null) {
            throw new IllegalStateException(
                    "M3UA AS not found: " + asName
            );
        }

        boolean assigned = false;

        for (Asp asp : targetAs.getAspList()) {

            if (asp.getAspFactory() != null
                    && aspName.equals(asp.getAspFactory().getName())) {

                assigned = true;
                break;
            }
        }

        if (!assigned) {
            m3uaManagement.assignAspToAs(asName, aspName);

            System.out.println(
                    "M3UA ASP assigned to AS: "
                            + aspName + " -> " + asName
            );
        } else {
            System.out.println(
                    "M3UA ASP already assigned to AS"
            );
        }
    }

    public void printAspState() {

        for (As as : m3uaManagement.getAppServers()) {

            System.out.println("AS: " + as.getName());

            for (Asp asp : as.getAspList()) {

                System.out.println(
                        "ASP: " + asp.getName()
                                + ", started=" + asp.isStarted()
                                + ", connected=" + asp.isConnected()
                                + ", up=" + asp.isUp()
                                + ", state=" + asp.getState()
                );
            }
        }

    }



    public void awaitReady(long timeout, TimeUnit unit)
            throws InterruptedException, TimeoutException {

        long deadline = System.nanoTime() + unit.toNanos(timeout);

        while (true) {

            for (As as : m3uaManagement.getAppServers()) {

                if (!asName.equals(as.getName())) {
                    continue;
                }

                if (!as.isUp()) {
                    break;
                }

                for (Asp asp : as.getAspList()) {

                    if (aspName.equals(asp.getName())
                            && asp.isConnected()
                            && asp.isUp()
                            && "ACTIVE".equals(asp.getState().toString())) {

                        System.out.println(
                                "M3UA READY: AS=" + as.getName()
                                        + ", AS state=" + as.getState()
                                        + ", ASP=" + asp.getName()
                                        + ", ASP state=" + asp.getState()
                        );

                        return;
                    }
                }
            }

            if (System.nanoTime() >= deadline) {
                throw new TimeoutException(
                        "M3UA AS/ASP did not become ACTIVE within "
                                + timeout + " " + unit
                );
            }

            long remainingNanos = deadline - System.nanoTime();

            TimeUnit.NANOSECONDS.sleep(
                    Math.min(
                            TimeUnit.MILLISECONDS.toNanos(100),
                            Math.max(1, remainingNanos)
                    )
            );
        }
    }




}

