package com.ilp2_ella_behan;

import com.ilp2_ella_behan.data.NextPositionRequest;
import com.ilp2_ella_behan.data.Position;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class AngleValidationTest {
    @LocalServerPort
    private int port;
    private final TestRestTemplate restTemplate = new TestRestTemplate();

    @Test
    void angleNotAllowed() {
        NextPositionRequest req = new NextPositionRequest();
        req.setStart(new Position(55.946233, -3.192473));
        req.setAngle(10.0);

        var response = restTemplate.postForEntity(
                "http://localhost:" + port + "/api/v1/nextPosition",
                req,
                String.class);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    void anglesAllowed() {
        double[] valid = {0, 22.5, 45, 90, 180, 337.5, 360};
        for (double a : valid) {
            NextPositionRequest req = new NextPositionRequest();
            req.setStart(new Position(55.946233, -3.192473));
            req.setAngle(a);

            var response = restTemplate.postForEntity(
                    "http://localhost:" + port + "/api/v1/nextPosition",
                    req,
                    Position.class);
            assertEquals(HttpStatus.OK, response.getStatusCode());
        }
    }

}