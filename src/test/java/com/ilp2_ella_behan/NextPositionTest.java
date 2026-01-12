package com.ilp2_ella_behan;

import com.ilp2_ella_behan.data.DistanceToRequest;
import com.ilp2_ella_behan.data.NextPositionRequest;
import com.ilp2_ella_behan.data.Position;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.test.web.servlet.MockMvc;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
class NextPositionTest {
    @Autowired
    private MockMvc mockMvc;
    @LocalServerPort
    private int port;

    private final TestRestTemplate restTemplate = new TestRestTemplate();
    @Test
    void nextPosition_output_correct() {
        NextPositionRequest request = new NextPositionRequest();
        request.setStart(new Position(55.946233,  -3.192473));
        request.setAngle(45.0);
        var response = restTemplate.postForEntity("http://localhost:" + port + "/api/v1/nextPosition",
                request,
                Position.class);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        Position next = response.getBody();
        assertEquals(-3.192367, next.getLng(), 1e-6);
        assertEquals(55.946339, next.getLat(), 1e-6);


    }
    @Test
    void nextPosition_statusCode_correct() {
        NextPositionRequest request = new NextPositionRequest();
        request.setStart(new Position(55.946233,  -3.192473));
        request.setAngle(45.0);
        var response = restTemplate.postForEntity("http://localhost:" + port + "/api/v1/nextPosition",
                request,
                Position.class);
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    void nextPosition_statusCode_missingStart() {
        NextPositionRequest request = new NextPositionRequest();
        request.setAngle(45.0);
        var response = restTemplate.postForEntity("http://localhost:" + port + "/api/v1/nextPosition",
                request,
                String.class);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }
    @Test
    void nextPosition_statusCode_missingAngle() {
        NextPositionRequest request = new NextPositionRequest();
        request.setStart(new Position(55.946233,  -3.192473));
        request.setAngle(Double.NaN);
        var response = restTemplate.postForEntity("http://localhost:" + port + "/api/v1/nextPosition",
                request,
                String.class);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    void nextPosition_statusCode_nullRequest() {
        DistanceToRequest request = new DistanceToRequest();
        var response = restTemplate.postForEntity("http://localhost:" + port + "/api/v1/nextPosition",
                request,
                String.class);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }
    @Test
    void nextPosition_output_correctSyntax() throws Exception {
        String requestBody = """
                {
                    "start": {
                        "lng": -3.192473,
                        "lat": 55.946233
                    },
                    "angle": 45.0
                }
                """;
        mockMvc.perform(post("/api/v1/nextPosition")
                        .contentType("application/json")
                        .content(requestBody))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.lng").value(-3.192367))
                .andExpect(jsonPath("$.lat").value(55.946339));

    }

    @Test
    void nextPosition_statusCode_correctSyntax() throws Exception {
        String requestBody = """
                {
                    "start": {
                        "lng": -3.192473,
                        "lat": 55.946233
                    },
                    "angle":45.0
                }
                
                """;

        mockMvc.perform(post("/api/v1/nextPosition")
                        .contentType("application/json")
                        .content(requestBody))
                .andExpect(status().isOk());
    }
    @Test
    void nextPosition_statusCode_badSyntax() throws Exception {
        String bad = """
                {
                    "start": {
                        "lng": -3.192473,
                        "lat": 55.946233
                    },
                    "angle":45.0
                """;

        mockMvc.perform(post("/api/v1/nextPosition")
                        .contentType("application/json")
                        .content(bad))
                .andExpect(status().isBadRequest());
    }
}
