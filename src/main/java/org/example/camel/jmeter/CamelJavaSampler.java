
package org.example.camel.jmeter;

import org.apache.jmeter.config.Arguments;
import org.apache.jmeter.protocol.java.sampler.AbstractJavaSamplerClient;
import org.apache.jmeter.protocol.java.sampler.JavaSamplerContext;
import org.apache.jmeter.samplers.SampleResult;

import org.example.camel.config.SigtranConfig;
import org.example.camel.generator.CapRequest;
import org.example.camel.registry.SigtranClientRegistry;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.ExecutionException;

public class CamelJavaSampler extends AbstractJavaSamplerClient {

    private static final String SERVICE_KEY = "SERVICE_KEY";
    private static final String RESPONSE_TIMEOUT_MS =
            "RESPONSE_TIMEOUT_MS";

    private SigtranClientRegistry.Lease lease;

    @Override
    public Arguments getDefaultParameters() {

        Arguments params = new Arguments();

        params.addArgument(SERVICE_KEY, "10");
        params.addArgument(RESPONSE_TIMEOUT_MS, "5000");

        return params;
    }

    @Override
    public void setupTest(JavaSamplerContext context) {

        try {
            SigtranConfig config = SigtranConfig.localStub();

            lease = SigtranClientRegistry.acquire(config);

        } catch (Exception e) {
            throw new IllegalStateException(
                    "Failed to initialize SIGTRAN client", e
            );
        }
    }

    @Override
    public SampleResult runTest(JavaSamplerContext context) {

        SampleResult result = new SampleResult();
        result.setSampleLabel("CAP InitialDP -> Continue");

        if (lease == null) {
            result.setSuccessful(false);
            result.setResponseCode("500");
            result.setResponseMessage("SIGTRAN client is not initialized");
            return result;
        }

        CapRequest request = null;

        result.sampleStart();

        try {
            int serviceKey = Integer.parseInt(
                    context.getParameter(SERVICE_KEY, "10")
            );

            long timeoutMs = Long.parseLong(
                    context.getParameter(RESPONSE_TIMEOUT_MS, "5000")
            );

            if (timeoutMs <= 0) {
                throw new IllegalArgumentException(
                        "RESPONSE_TIMEOUT_MS must be positive"
                );
            }

            request = lease.getManager()
                    .getCapClient()
                    .sendInitialDP(serviceKey);

            request.response().get(
                    timeoutMs,
                    TimeUnit.MILLISECONDS
            );

            result.setSuccessful(true);
            result.setResponseCodeOK();
            result.setResponseMessage("Continue received");

            result.setResponseData(
                    "Dialog ID: " + request.dialogId()
                            + "\nResult: SUCCESS",
                    "UTF-8"
            );

        } catch (TimeoutException e) {

            result.setSuccessful(false);
            result.setResponseCode("408");
            result.setResponseMessage(
                    "Timeout waiting for CAP Continue"
            );

        } catch (InterruptedException e) {

            Thread.currentThread().interrupt();

            result.setSuccessful(false);
            result.setResponseCode("499");
            result.setResponseMessage("Sampler interrupted");

        } catch (ExecutionException e) {

            result.setSuccessful(false);
            result.setResponseCode("500");
            result.setResponseMessage(
                    "CAP response error: " + e.getCause()
            );

        } catch (Exception e) {

            result.setSuccessful(false);
            result.setResponseCode("500");
            result.setResponseMessage(
                    "CAP request failed: " + e
            );

        } finally {

            if (request != null
                    && !request.response().isDone()) {

                lease.getManager()
                        .getCapClient()
                        .cancelRequest(request.dialogId());
            }

            result.sampleEnd();
        }

        return result;
    }

    @Override
    public void teardownTest(JavaSamplerContext context) {

        if (lease != null) {
            lease.close();
            lease = null;
        }
    }
}

