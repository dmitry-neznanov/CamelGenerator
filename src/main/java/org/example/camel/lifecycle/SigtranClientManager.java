
package org.example.camel.lifecycle;

import org.example.camel.config.SigtranConfig;
import org.example.camel.generator.*;

import java.util.Objects;

public class SigtranClientManager implements AutoCloseable {

    private final SigtranConfig config;

    private SctpClient sctpClient;
    private M3uaClient m3uaClient;
    private SccpClient sccpClient;
    private TcapClient tcapClient;
    private CapClient capClient;

    private volatile boolean running = false;

    public SigtranClientManager(SigtranConfig config) {
        this.config = Objects.requireNonNull(config);
    }

    public synchronized void start() {

        if (running) {
            return;
        }

        try {
            sctpClient = new SctpClient(config);
            sctpClient.start();

            m3uaClient = new M3uaClient(
                    sctpClient.getManagement(),
                    sctpClient.getAssociationName(),
                    config
            );
            m3uaClient.start();

            sccpClient = new SccpClient(
                    m3uaClient.getManagement(),
                    config
            );
            sccpClient.start();

            tcapClient = new TcapClient(
                    sccpClient.getStack().getSccpProvider()
            );

            capClient = new CapClient(
                    tcapClient.getStack().getProvider(),
                    sccpClient.getStack().getSccpProvider(),
                    config
            );

            capClient.start();

            m3uaClient.startAsp();

            running = true;

            System.out.println(
                    "SIGTRAN client started: " + config.stackName()
            );

        } catch (Exception e) {
            stop();

            throw new IllegalStateException(
                    "Failed to start SIGTRAN client", e
            );
        }
    }

    public synchronized void stop() {

        running = false;

        try {
            if (capClient != null) {
                capClient.stop();
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            capClient = null;
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

    public boolean isRunning() {
        return running;
    }

    public CapClient getCapClient() {
        if (!running) {
            throw new IllegalStateException(
                    "SIGTRAN client is not running"
            );
        }

        return capClient;
    }

    @Override
    public void close() {
        stop();
    }

    public void printM3uaState() {
        m3uaClient.printAspState();
    }
}
