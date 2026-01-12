package com.ilp2_ella_behan;

import com.ilp2_ella_behan.data.Position;
import com.ilp2_ella_behan.service.Geometry;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;


public class GeometrySafetyTest {

    private static List<Position> square(double lat, double lng, double half) {
        return List.of(
                new Position(lat - half, lng - half),
                new Position(lat - half, lng + half),
                new Position(lat + half, lng + half),
                new Position(lat + half, lng - half),
                new Position(lat - half, lng - half)
        );
    }

    @Test
    void isInPolygon_pointInside_true() {
        var poly = square(55.945000,-3.188000,0.00025);
        var p = new Position(55.945000, -3.188000);
        assertTrue(Geometry.isInPolygon(p,poly));
    }

    @Test
    void isInPolygon_pointOutside_false() {
        var poly = square(55.945000,-3.188000,0.00025);
        var p = new Position(55.946000, -3.188000);
        assertFalse(Geometry.isInPolygon(p,poly));
    }

    @Test
    void isInPolygon_pointOnBorder_true() {
        var poly = square(55.945000,-3.188000,0.00025);
        var p = new Position(55.945000+0.00025, -3.188000);
        assertTrue(Geometry.isInPolygon(p,poly));
    }

    @Test
    void pathCrossesRegion_segmentCrossing_true() {
        var poly = square(55.945000,-3.188000,0.00025);
        var start = new Position(55.945000, -3.188800);
        var end = new Position(55.945000, -3.187200);
        assertTrue(Geometry.pathCrossesRegion(start,end,poly));
    }

    @Test
    void pathCrossesRegion_segmentFarAway_false() {
        var poly = square(55.945000,-3.188000,0.00025);
        var start = new Position(55.947000, -3.188800);
        var end = new Position(55.947000, -3.187200);
        assertFalse(Geometry.pathCrossesRegion(start,end,poly));
    }

    @Test
    void pathCrossesRegion_segmentOnBorder_true() {
        var poly = square(55.945000,-3.188000,0.00025);
        var start = new Position(55.945000, -3.188800);
        var end = new Position(55.945000, -3.18800 - 0.00025);
        assertTrue(Geometry.pathCrossesRegion(start,end,poly));
    }
}
