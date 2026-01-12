package com.ilp2_ella_behan;

import com.ilp2_ella_behan.data.*;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
class IsInRegionTest {
    @Autowired
    private MockMvc mockMvc;
    @LocalServerPort
    private int port;

    private final TestRestTemplate restTemplate = new TestRestTemplate();
    @Test
    void isInRegion_output_correct() {
        Region region = new Region();
        region.setName("central");
        region.setVertices(List.of(
                new Position(55.946233, -3.192473),
                new Position(55.942617, -3.192473),
                new Position(55.942617, -3.184319),
                new Position(55.946233, -3.184319),
                new Position(55.946233, -3.192473)));
        IsInRegionRequest req= new IsInRegionRequest();
        req.setPosition(new Position(55.944000, -3.188000));//point within region
        req.setRegion(region);
        req.setName("central");

        var response = restTemplate.postForEntity("http://localhost:" + port + "/api/v1/isInRegion",
                req,
                Boolean.class);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(Boolean.TRUE, response.getBody());

    }
    @Test
    void isInRegion_statusCode_correct() {
        Region region = new Region();
        region.setName("central");
        region.setVertices(List.of(
                new Position(55.946233, -3.192473),
                new Position(55.942617, -3.192473),
                new Position(55.942617, -3.184319),
                new Position(55.946233, -3.184319),
                new Position(55.946233, -3.192473)));
        IsInRegionRequest req= new IsInRegionRequest();
        req.setPosition(new Position(55.944000, -3.188000));//point within region
        req.setRegion(region);
        req.setName("central");

        var response = restTemplate.postForEntity("http://localhost:" + port + "/api/v1/isInRegion",
                req,
                String.class);
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    void isInRegion_statusCode_missingPos() {
        Region region = new Region();
        region.setName("central");
        region.setVertices(List.of(
                new Position(55.946233, -3.192473),
                new Position(55.942617, -3.192473),
                new Position(55.942617, -3.184319),
                new Position(55.946233, -3.184319),
                new Position(55.946233, -3.192473)));
        IsInRegionRequest req= new IsInRegionRequest();
        req.setRegion(region);
        req.setName("central");

        var response = restTemplate.postForEntity("http://localhost:" + port + "/api/v1/isInRegion",
                req,
                String.class);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }
    @Test
    void isInRegion_statusCode_missingRegion() {
        IsInRegionRequest req = new IsInRegionRequest();
        req.setPosition(new Position(55.944000,  -3.188000));
        req.setName("central");

        var response = restTemplate.postForEntity("http://localhost:" + port + "/api/v1/isInRegion",
                req,
                String.class);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    void isInRegion_statusCode_nullRequest() {
        DistanceToRequest request = new DistanceToRequest();
        var response = restTemplate.postForEntity("http://localhost:" + port + "/api/v1/isInRegion",
                request,
                String.class);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }
    @Test
    void isInRegion_output_correctSyntax() throws Exception {
        String requestBody = """
                {
                   "name": "central",
                   "position": { "lng": -3.1880000, "lat": 55.944000 },
                   "region": {
                     "name": "central",
                     "vertices": [
                       { "lng": -3.192473, "lat": 55.946233 },
                       { "lng": -3.192473, "lat": 55.942617 },
                       { "lng": -3.184319, "lat": 55.942617 },
                       { "lng": -3.184319, "lat": 55.946233 },
                       { "lng": -3.192473, "lat": 55.946233 }
                     ]
                   }
                 }
                """;
        mockMvc.perform(post("/api/v1/isInRegion")
                        .contentType("application/json")
                        .content(requestBody))
                .andExpect(status().isOk())
                .andExpect(content().string("true"));
    }

    @Test
    void isInRegion_statusCode_correctSyntax() throws Exception {
        String requestBody = """
                {
                   "name": "central",
                   "position": { "lng": -3.1880000, "lat": 55.944000 },
                   "region": {
                     "name": "central",
                     "vertices": [
                       { "lng": -3.192473, "lat": 55.946233 },
                       { "lng": -3.192473, "lat": 55.942617 },
                       { "lng": -3.184319, "lat": 55.942617 },
                       { "lng": -3.184319, "lat": 55.946233 },
                       { "lng": -3.192473, "lat": 55.946233 }
                     ]
                   }
                 }
                """;

        mockMvc.perform(post("/api/v1/isInRegion")
                        .contentType("application/json")
                        .content(requestBody))
                .andExpect(status().isOk());
    }
    @Test
    void isInRegion_statusCode_badSyntax() throws Exception {
        String bad = """
                {
                "position": {
                "lng":-3.1880000,
                "lat":55.944000
                },
                 "region": {
                 "name": "central",
                 "vertices": [
                 {
                 "lng": -3.192473,
                 "lat": 55.946233
                 },
                  {
                  "lng": -3.192473,
                  "lat": 55.942617
                  },
                  {
                  "lng": -3.184319,
                  "lat": 55.942617
                  },
                  {
                  "lng": -3.184319,
                  "lat": 55.946233
                  },
                  {
                  "lng": -3.192473,
                  "lat": 55.946233
                  }
                  ]
                  }
                 
                """;

        mockMvc.perform(post("/api/v1/isInRegion")
                        .contentType("application/json")
                        .content(bad))
                .andExpect(status().isBadRequest());
    }
}
