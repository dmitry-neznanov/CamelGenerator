package org.example.camel.controller;

import org.example.camel.lifecycle.SigtranLifecycle;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/cap")
public class CapController {

    private final SigtranLifecycle lifecycle;

    public CapController(SigtranLifecycle lifecycle) {
        this.lifecycle = lifecycle;
    }

    @PostMapping("/initial-dp")
    public String sendInitialDP() throws Exception {

        long dialogId = lifecycle.getCapClient().sendInitialDP();

        return "InitialDP sent. Dialog ID=" + dialogId;
    }
}
