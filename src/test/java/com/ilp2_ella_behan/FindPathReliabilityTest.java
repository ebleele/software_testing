package com.ilp2_ella_behan;

import com.ilp2_ella_behan.data.Position;
import com.ilp2_ella_behan.data.RestrictedArea;
import com.ilp2_ella_behan.service.IlpDroneService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestTemplate;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(classes = { IlpDroneService.class, FindPathReliabilityTest.TestBeans.class })
class FindPathReliabilityTest {

    @Autowired
    private IlpDroneService service;

    @TestConfiguration
    static class TestBeans {
        @Bean
        RestTemplate restTemplate() {return new RestTemplate();}

        @Bean
        @Qualifier("ilpEndpoint")
        String ilpEndpoint() {return "http://localhost:9999";}
    }

    @Test
    void findPath_sameInputs_sameOutputs() {
        Position start  = new Position(55.944000, -3.188000);
        Position target = new Position(55.946000, -3.188000);

        List<RestrictedArea> areas =
                List.of(TestData.squareRestrictedArea(55.945000, -3.188000, 0.00025));

        List<Position> p1 = service.findPath(start, target, areas);
        List<Position> p2 = service.findPath(start, target, areas);

        assertSamePath(p1, p2);
    }

    private static void assertSamePath(List<Position> p1, List<Position> p2) {
        assertNotNull(p1);
        assertNotNull(p2);
        assertEquals(p1.size(), p2.size());

        for (int i = 0; i < p1.size(); i++) {
            Position a = p1.get(i);
            Position b = p2.get(i);

            assertNotNull(a);
            assertNotNull(b);

            // Double -> auto-unboxed to primitive double here
            assertEquals(a.getLat(), b.getLat(), 1e-9);
            assertEquals(a.getLng(), b.getLng(), 1e-9);
        }
    }
}
