package org.example.camel.generator;

import org.example.camel.lifecycle.SigtranLifecycle;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TcapController {

    private final SigtranLifecycle sigtranLifecycle;

    public TcapController(SigtranLifecycle sigtranLifecycle) {
        this.sigtranLifecycle = sigtranLifecycle;
    }

    @PostMapping("/api/tcap/send")
    public String send() throws Exception {


        return "TCAP BEGIN sent";
    }
}

