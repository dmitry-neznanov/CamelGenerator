
package org.example.camel;

import org.example.camel.generator.SctpClient;
import org.example.camel.generator.M3uaClient;

public class GeneratorApplication {

    public static void main(String[] args) throws Exception {

        SctpClient sctpClient = new SctpClient();
        sctpClient.start();

        M3uaClient m3uaClient = new M3uaClient(
                sctpClient.getManagement(),
                sctpClient.getAssociationName()
        );

        m3uaClient.start();
        m3uaClient.startAsp();

        System.out.println("CamelGenerator started");
    }
}
