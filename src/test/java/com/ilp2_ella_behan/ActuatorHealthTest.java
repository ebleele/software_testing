package com.ilp2_ella_behan;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;


import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class ActuatorHealthTest {
    @LocalServerPort
    private int port;

    private final TestRestTemplate restTemplate = new TestRestTemplate();

    @Test
    void health_returns_UP() {
        var body = restTemplate.getForObject(
                "http://localhost:" + port + "/actuator/health",
                String.class
        );
        assertTrue(body.contains("\"status\":\"UP\"") || body.contains("UP"));
    }

    @Test
    void health_statusCodes_correct() {
        var response = restTemplate.getForEntity(
                "http://localhost:" + port + "/actuator/health",
                String.class
        );
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

}
