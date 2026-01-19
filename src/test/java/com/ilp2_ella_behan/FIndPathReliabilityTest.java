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

@SpringBootTest(classes = {IlpDroneService.class, FindPathReliabilityTest.TestBeans.class})
class FindPathReliabilityTest {
    @Autowired
    private IlpDroneService service;

    @TestConfiguration
    static class TestBeans {
        @Bean
        RestTemplate restTemplate() { return new RestTemplate();}
        @Bean @Qualifier("ilpEndpoint") String ilpEndpoint(){return "http://localhost:9999";}
    }

    @Test
    void findPath_sameInputs_sameOutputs() {
        Position start = new Position(55.944000, -3.188000);
        Position target = new Position(55.946000, -3.188000);
        List<RestrictedArea> areas = List.of(TestData.squareRestrictedArea(55.945000, -3.188000, 0.00025));

        List<Position> p1 = service.findPath(start, target, areas);
        List<Position> p2 = service.findPath(start, target, areas);

        assertEquals(p1, p2);
    }
}