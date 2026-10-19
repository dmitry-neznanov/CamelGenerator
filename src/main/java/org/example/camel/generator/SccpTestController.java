package org.example.camel.generator;

import org.example.camel.lifecycle.SigtranLifecycle;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class SccpTestController {

    private final SigtranLifecycle lifecycle;

    public SccpTestController(SigtranLifecycle lifecycle) {
        this.lifecycle = lifecycle;
    }

    @PostMapping("/api/sccp/send")
    public String send() throws Exception {

        lifecycle.getSccpClient().sendTestMessage();

        return "SCCP message submitted";
    }
}
