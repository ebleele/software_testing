package com.ilp2_ella_behan;

import com.ilp2_ella_behan.data.Position;
import com.ilp2_ella_behan.data.RestrictedArea;
import com.ilp2_ella_behan.service.Geometry;
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

@SpringBootTest(classes = {
        IlpDroneService.class,
        FindPathSafetyTest.TestBeans.class
})

public class FindPathSafetyTest {
    @Autowired
    private IlpDroneService service;
    @TestConfiguration
    static class TestBeans {
        @Bean
        RestTemplate restTemplate() {
            return new RestTemplate();
        }
        @Bean
        @Qualifier("ilpEndpoint")
        String ilpEndpoint() {
            return "http://localhost:8080";
        }
    }

    private static void assertSafe(List<Position> path, List<RestrictedArea> areas){
        assertNotNull(path);
        assertFalse(path.isEmpty());

        for (RestrictedArea area : areas) {
            List<Position> poly = area.getVertices();
            //no points inside border
            for (Position p : path) {
                assertFalse(Geometry.isInPolygon(p, poly));
            }
            //doesn't cross/touch border
            for (int i = 0; i + 1 < path.size(); i++) {
                Position a = path.get(i);
                Position b = path.get(i+1);
                assertFalse(Geometry.pathCrossesRegion(a, b, poly));//CHANGED MESSD UP
            }
        }
    }

    @Test
    void findPath_simpleReachesTarget_safe() {
        Position start = new Position(55.944000,-3.188000);
        Position target = new Position(55.945000, - 3.187000);

        List<RestrictedArea> areas = List.of();
        List<Position> path = service.findPath(start, target, areas);

        assertSafe(path, areas);
        assertTrue(Geometry.isCloseTo(path.getLast(), target));
    }

    @Test
    void findPath_blockedPath_safe(){
        Position start = new Position(55.944000,-3.188000);
        Position target = new Position(55.946000, - 3.188000);

        RestrictedArea block = TestData.squareRestrictedArea(55.945000, -3.188000, 0.00025);
        List<RestrictedArea> areas = List.of(block);

        List<Position> path = service.findPath(start, target, areas);

        assertSafe(path, areas);
        assertFalse(path.isEmpty());
    }

    @Test
    void findPath_startInRestrictedArea_safe() {
        RestrictedArea block = TestData.squareRestrictedArea(5.945000, -3.188000, 0.00025);
        List<RestrictedArea> areas = List.of(block);

        Position start = new Position(55.945000,-3.188000);
        Position target = new Position(55.946000, - 3.188000);

        List<Position> path = service.findPath(start, target, areas);

        assertNotNull(path);
        assertFalse(path.isEmpty());
        assertSafe(path, areas);
    }
}
