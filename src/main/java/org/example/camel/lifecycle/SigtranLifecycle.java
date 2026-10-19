package org.example.camel.lifecycle;

import org.example.camel.generator.SccpClient;
import org.example.camel.generator.SctpClient;
import org.example.camel.generator.M3uaClient;
import org.springframework.context.SmartLifecycle;
import org.springframework.stereotype.Component;

@Component
public class SigtranLifecycle implements SmartLifecycle {

    private SctpClient sctpClient;
    private M3uaClient m3uaClient;
    private SccpClient sccpClient;


    private volatile boolean running = false;

    @Override
    public synchronized void start() {

        if (running) {
            return;
        }

        try {
            // 1. SCTP
            sctpClient = new SctpClient();
            sctpClient.start();

            // 2. M3UA
            m3uaClient = new M3uaClient(
                    sctpClient.getManagement(),
                    sctpClient.getAssociationName()
            );

            m3uaClient.start();

            sccpClient = new SccpClient(m3uaClient.getManagement());
            sccpClient.start();

            m3uaClient.startAsp();

            running = true;

            System.out.println("CamelGenerator SIGTRAN stack started");

        } catch (Exception e) {
            stop();
            throw new IllegalStateException(
                    "Failed to start SIGTRAN stack", e
            );
        }
    }

    @Override
    public synchronized void stop() {

        running = false;

        try {
            if (sccpClient != null) {
                sccpClient.stop();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            if (m3uaClient != null) {
                m3uaClient.stop();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            if (sctpClient != null) {
                sctpClient.stop();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean isRunning() {
        return running;
    }

    @Override
    public int getPhase() {
        return 0;
    }

    public SccpClient getSccpClient() {
        return sccpClient;
    }
}