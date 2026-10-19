
package org.example.camel.generator;

import org.mobicents.protocols.sctp.ManagementImpl;
import org.restcomm.protocols.ss7.m3ua.ExchangeType;
import org.restcomm.protocols.ss7.m3ua.Functionality;
import org.restcomm.protocols.ss7.m3ua.impl.M3UAManagementImpl;
import org.restcomm.protocols.ss7.m3ua.impl.parameter.ParameterFactoryImpl;
import org.restcomm.protocols.ss7.m3ua.parameter.ParameterFactory;
import org.restcomm.protocols.ss7.m3ua.parameter.RoutingContext;
import org.restcomm.protocols.ss7.m3ua.parameter.TrafficModeType;

public class M3uaClient {

    private static final String MANAGEMENT_NAME = "CamelGeneratorM3ua";
    private static final String MANAGEMENT_PATH = "CamelGenerator";

    private static final String AS_NAME = "generatorAS";
    private static final String ASP_NAME = "generatorASP";

    private static final long ROUTING_CONTEXT = 100L;

    private static final int REMOTE_POINT_CODE = 2;

    private static final int MIN_ASP_ACTIVE = 1;

    private static final int WILDCARD_OPC = -1;
    private static final int WILDCARD_SI = -1;

    private static final Functionality FUNCTIONALITY = Functionality.AS;
    private static final ExchangeType EXCHANGE_TYPE = ExchangeType.SE;
    private static final int TRAFFIC_MODE = TrafficModeType.Loadshare;

    private final M3UAManagementImpl m3uaManagement;
    private final String associationName;

    public M3uaClient(ManagementImpl sctpManagement,
                      String associationName) {

        this.associationName = associationName;

        m3uaManagement = new M3UAManagementImpl(
                MANAGEMENT_NAME,
                MANAGEMENT_PATH,
                null
        );

        m3uaManagement.setTransportManagement(sctpManagement);
    }

    public void start() throws Exception {

        m3uaManagement.start();

        ParameterFactory factory = new ParameterFactoryImpl();

        RoutingContext routingContext =
                factory.createRoutingContext(
                        new long[]{ROUTING_CONTEXT}
                );

        TrafficModeType trafficMode =
                factory.createTrafficModeType(TRAFFIC_MODE);

        boolean asExists = false;

        for (var as : m3uaManagement.getAppServers()) {
            if (AS_NAME.equals(as.getName())) {
                asExists = true;
                break;
            }
        }

        if (!asExists) {
            m3uaManagement.createAs(
                    AS_NAME,
                    FUNCTIONALITY,
                    EXCHANGE_TYPE,
                    null,
                    routingContext,
                    trafficMode,
                    MIN_ASP_ACTIVE,
                    null
            );
        }

        boolean aspExists = false;

        for (var asp : m3uaManagement.getAspfactories()) {
            if (ASP_NAME.equals(asp.getName())) {
                aspExists = true;
                break;
            }
        }

        if (!aspExists) {
            m3uaManagement.createAspFactory(
                    ASP_NAME,
                    associationName
            );

            m3uaManagement.assignAspToAs(
                    AS_NAME,
                    ASP_NAME
            );
        }

        if (m3uaManagement.getRoute().isEmpty()) {
            m3uaManagement.addRoute(
                    REMOTE_POINT_CODE,
                    WILDCARD_OPC,
                    WILDCARD_SI,
                    AS_NAME
            );
        }

        System.out.println("M3UA client configured");
    }

    public void startAsp() throws Exception {

        boolean started = false;

        for (var asp : m3uaManagement.getAspfactories()) {
            if (ASP_NAME.equals(asp.getName())) {
                started = asp.getStatus();
                break;
            }
        }

        if (!started) {
            m3uaManagement.startAsp(ASP_NAME);
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
}

