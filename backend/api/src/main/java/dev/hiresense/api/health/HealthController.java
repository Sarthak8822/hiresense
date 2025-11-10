package dev.hiresense.api.health;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

record HealthDTO(String status) {}

@RestController
public class HealthController {
    @GetMapping("/health")
    public HealthDTO health() {
        return new HealthDTO("OK");
    }
}
