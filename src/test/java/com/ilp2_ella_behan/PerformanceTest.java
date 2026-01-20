package com.ilp2_ella_behan;

import com.ilp2_ella_behan.data.*;
import com.ilp2_ella_behan.data.RestrictedArea;
import com.ilp2_ella_behan.service.IlpDroneService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestTemplate;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
//intellij doesnt like this for some reason, cant find it, but maven can do it just fine
@SpringBootTest( classes = {IlpDroneService.class, com.ilp2_ella_behan.PerformanceTest.TestBeans.class})
public class PerformanceTest {
    @Autowired
    private  IlpDroneService service;
    @Autowired
    private RestTemplate restTemplate;

    @TestConfiguration
    static class TestBeans {
        @Bean
        RestTemplate restTemplate() {
            return mock(RestTemplate.class);
        }

        @Bean
        @Qualifier("ilpEndpoint")
        String ilpEndpoint() {
            return "http://test-ilp";
        }
    }

    @Test
    void calcDeliveryPathSmall_In30Seconds() {
        when(restTemplate.getForObject(eq("http://test-ilp/restricted-areas"), eq(RestrictedArea[].class)))
                .thenReturn(new RestrictedArea[]{TestData.squareRestrictedArea(55.9450,-3.1880,0.00025)});

        when(restTemplate.getForObject(eq("http://test-ilp/drones"), eq(Drone[].class)))
                .thenReturn(new Drone[]{drone("D1", true, false), drone("D2", false, true)});

        when(restTemplate.getForObject(eq("http://test-ilp/drones-for-service-points"), eq(DroneForServicePoint[].class)))
                .thenReturn(new DroneForServicePoint[]{mapping(1, "D1", "D2")});

        when(restTemplate.getForObject(eq("http://test-ilp/service-points"), eq(ServicePoint[].class)))
                .thenReturn(new ServicePoint[]{servicePoint(1, 55.9445, -3.1885)});

        when(restTemplate.postForObject(anyString(), any(), eq(String[].class)))
                .thenReturn(new String[]{"D1", "D2"});

        List<MedDispatchRec> dispatches = List.of(
                dispatch(1, 55.9440, -3.1880, reqCooling()),
                dispatch(2, 55.9450, -3.1875, reqCooling()),
                dispatch(3, 55.9460, -3.1885, reqCooling())
        );

        assertTimeoutPreemptively(Duration.ofSeconds(30), () -> {
            service.calcDeliveryPath(dispatches);
        });
    }

    @Test
    void calcDeliveryPathLarger_30Seconds() {
        when(restTemplate.getForObject(eq("http://test-ilp/restricted-areas"), eq(RestrictedArea[].class)))
                .thenReturn((new RestrictedArea[]{TestData.squareRestrictedArea(55.9450,-3.1880,0.00025)}));

        when(restTemplate.getForObject(eq("http://test-ilp/drones"), eq(Drone[].class)))
                .thenReturn(new Drone[]{drone("D1", true, false), drone("D2", false, true)});

        when(restTemplate.getForObject(eq("http://test-ilp/drones-for-service-points"), eq(DroneForServicePoint[].class)))
                .thenReturn(new DroneForServicePoint[]{mapping(1, "D1", "D2")});

        when(restTemplate.getForObject(eq("http://test-ilp/service-points"), eq(ServicePoint[].class)))
                .thenReturn(new ServicePoint[]{servicePoint(1, 55.9445, -3.1885)});

        when(restTemplate.postForObject(anyString(), any(), eq(String[].class)))
                .thenReturn(new String[]{"D1", "D2"});

        List<MedDispatchRec> dispatches = IntStream.rangeClosed(1, 50)
                        .mapToObj(i-> {
                        double lat = 55.9440 + (i * 0.00005);
                        double lng = -3.1880 + (i * 0.00005);

                        Requirements req;
                        if (i % 3 == 0) {
                            req = reqCooling();
                        } else if (i % 3 == 1) {
                            req = reqHeating();
                        } else {
                            req = reqNone();
                        }

                        return dispatch(i, lat, lng,req);
                        }).collect(Collectors.toList());

        assertTimeoutPreemptively(Duration.ofSeconds(30), () -> {
            service.calcDeliveryPath(dispatches);
        });
    }

    private static Drone drone(String id, boolean cooling, boolean heating) {
        DroneCapability caps = new DroneCapability();
        caps.setMaxMoves(10_000);
        caps.setCooling(cooling);
        caps.setHeating(heating);
        caps.setCostInitial(1.0);
        caps.setCostFinal(1.0);
        caps.setCostPerMove(0.1);

        Drone d = new Drone();
        d.setId(id);
        d.setCapability(caps);
        return d;
    }

    private static  DroneForServicePoint mapping(int servicePointId, String... droneIds) {
        DroneForServicePoint m = new DroneForServicePoint();
        m.setServicePointId(servicePointId);

        List<DroneAvailabilityItem> items = java.util.Arrays.stream(droneIds).map(id -> {
            DroneAvailabilityItem it = new DroneAvailabilityItem();
            it.setId(id);
            it.setAvailability(List.of());
            return it;
        }).toList();
        m.setDrones(items);
        return m;
    }

    private static ServicePoint servicePoint(int id, double lat, double lng) {
        ServicePoint sp = new ServicePoint();
        sp.setId(id);
        sp.setLocation(new Position(lat, lng));
        return sp;
    }

    private static MedDispatchRec dispatch(int id, double lat, double lng, Requirements req){
        MedDispatchRec r = new MedDispatchRec();
        r.setId(id);
        r.setDate(LocalDate.of(2025, 1, 1));
        r.setTime(LocalTime.of(12,0));
        r.setRequirements(req);
        r.setDelivery(new Position(lat, lng));
        return r ;
    }

    private static Requirements reqCooling() {
        Requirements r = new Requirements();
        r.setCooling(true);
        r.setHeating(false);
        return r;
    }

    private static Requirements reqHeating() {
        Requirements r = new Requirements();
        r.setCooling(false);
        r.setHeating(true);
        return r;
    }

    private static Requirements reqNone() {
        Requirements r = new Requirements();
        r.setCooling(false);
        r.setHeating(false);
        return r;
    }




}
