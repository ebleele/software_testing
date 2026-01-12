package com.ilp2_ella_behan;

import com.ilp2_ella_behan.data.DistanceToRequest;
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
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
class IsCloseToTest {
    @Autowired
    private MockMvc mockMvc;
    @LocalServerPort
    private int port;

    private final TestRestTemplate restTemplate = new TestRestTemplate();
    @Test
    void isCloseTo_output_correct() {
        DistanceToRequest request = new DistanceToRequest();
        request.setPosition1(new Position(55.946233,  -3.192473));
        request.setPosition2(new Position(55.942617,  -3.192473));
        var response = restTemplate.postForEntity("http://localhost:" + port + "/api/v1/isCloseTo",
                request,
                Boolean.class);
        assertEquals(false, response.getBody());
    }
    @Test
    void isCloseTo_statusCode_correct() {
        DistanceToRequest request = new DistanceToRequest();
        request.setPosition1(new Position(55.946233,  -3.192473));
        request.setPosition2(new Position(55.942617,  -3.192473));
        var response = restTemplate.postForEntity("http://localhost:" + port + "/api/v1/isCloseTo",
                request,
                String.class);
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    void isCloseTo_statusCode_missingP1() {
        DistanceToRequest request = new DistanceToRequest();
        request.setPosition2(new Position(55.942617,  -3.192473));
        var response = restTemplate.postForEntity("http://localhost:" + port + "/api/v1/isCloseTo",
                request,
                String.class);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }
    @Test
    void isCloseTo_statusCode_missingP2() {
        DistanceToRequest request = new DistanceToRequest();
        request.setPosition1(new Position(55.942617,  -3.192473));
        var response = restTemplate.postForEntity("http://localhost:" + port + "/api/v1/isCloseTo",
                request,
                String.class);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }
    @Test
    void isCloseTo_statusCode_nullLat() {
        DistanceToRequest request = new DistanceToRequest();
        request.setPosition1(new Position(null,  -3.192473));
        request.setPosition2(new Position(55.942617,  -3.192473));
        var response = restTemplate.postForEntity("http://localhost:" + port + "/api/v1/isCloseTo",
                request,
                String.class);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }
    @Test
    void isCloseTo_statusCode_nullLng() {
        DistanceToRequest request = new DistanceToRequest();
        request.setPosition1(new Position(55.946233,  null));
        request.setPosition2(new Position(55.942617,  -3.192473));
        var response = restTemplate.postForEntity("http://localhost:" + port + "/api/v1/isCloseTo",
                request,
                String.class);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }
    @Test
    void isCloseTo_statusCode_nullRequest() {
        DistanceToRequest request = new DistanceToRequest();
        var response = restTemplate.postForEntity("http://localhost:" + port + "/api/v1/isCloseTo",
                request,
                String.class);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }
    @Test
    void isCloseTo_output_correctSyntax() throws Exception {
        String requestBody = """
                {
                    "position1": {
                        "lng": -3.192473,
                        "lat": 55.946233
                    },
                    "position2": {
                        "lng": -3.192473,
                        "lat": 55.942617
                    }
                }

                """;
        mockMvc.perform(post("/api/v1/isCloseTo")
                        .contentType("application/json")
                        .content(requestBody))
                .andExpect(content().string("false"));
    }

    @Test
    void isCloseTo_statusCode_correctSyntax() throws Exception {
        String requestBody = """
                {
                    "position1": {
                        "lng": -3.192473,
                        "lat": 55.946233
                    },
                    "position2": {
                        "lng": -3.192473,
                        "lat": 55.942617
                    }
                }
                
                """;

        mockMvc.perform(post("/api/v1/isCloseTo")
                        .contentType("application/json")
                        .content(requestBody))
                .andExpect(status().isOk());
    }
    @Test
    void isCloseTo_statusCode_badSyntax() throws Exception {
        String requestBody = """
                {
                    "position1": {
                        "lng": -3.192473,
                        "lat": 55.946233
                    },
                    "position2": {
                        "lng": -3.192473,
                        "lat": 55.942617
                    }
                
                
                """;

        mockMvc.perform(post("/api/v1/isCloseTo")
                        .contentType("application/json")
                        .content(requestBody))
                .andExpect(status().isBadRequest());
    }
}
