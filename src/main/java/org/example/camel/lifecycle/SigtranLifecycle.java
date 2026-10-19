
package org.example.camel.lifecycle;

import org.example.camel.generator.SccpClient;
import org.example.camel.generator.SctpClient;
import org.example.camel.generator.M3uaClient;
import org.example.camel.generator.TcapClient;
import org.springframework.context.SmartLifecycle;
import org.springframework.stereotype.Component;

@Component
public class SigtranLifecycle implements SmartLifecycle {

    private SctpClient sctpClient;
    private M3uaClient m3uaClient;
    private SccpClient sccpClient;
    private TcapClient tcapClient;

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

            // 3. SCCP
            sccpClient = new SccpClient(
                    m3uaClient.getManagement()
            );
            sccpClient.start();

            // 4. TCAP
            tcapClient = new TcapClient(
                    sccpClient.getStack().getSccpProvider()
            );
            tcapClient.start();

            // 5. Запускаем M3UA ASP
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

        // Останавливаем компоненты в обратном порядке

        try {
            if (tcapClient != null) {
                tcapClient.stop();
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            tcapClient = null;
        }

        try {
            if (sccpClient != null) {
                sccpClient.stop();
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            sccpClient = null;
        }

        try {
            if (m3uaClient != null) {
                m3uaClient.stop();
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            m3uaClient = null;
        }

        try {
            if (sctpClient != null) {
                sctpClient.stop();
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            sctpClient = null;
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

    public TcapClient getTcapClient() {
        return tcapClient;
    }
}
