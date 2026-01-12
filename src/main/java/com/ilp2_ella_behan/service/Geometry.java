package com.ilp2_ella_behan.service;

import com.ilp2_ella_behan.data.Position;
import org.springframework.stereotype.Service;

import java.awt.geom.Line2D;
import java.util.List;

import static com.ilp2_ella_behan.service.RequestValidator.round;

@Service
public class Geometry {
    public static final double singleMove = 0.00015;

//Calculates Euclidean distance between two points using
    public static double distance(Position a, Position b) {
        double x = a.getLng() - b.getLng();
        double y = a.getLat() - b.getLat();
        return Math.sqrt(x * x + y * y);
    }

    public static boolean isCloseTo(Position a, Position b) {
        return (round(distance(a, b))< singleMove);
    }

//Calculates next position using each drone move as 0.00015 using trigonometry
    public static Position nextPosition(Position start, double angle) {
        double endLng = round(start.getLng() + singleMove * Math.cos(Math.toRadians(angle)));
        double endLat = round(start.getLat() + singleMove * Math.sin(Math.toRadians(angle)));

        Position next = new Position();
        next.setLng(endLng);
        next.setLat(endLat);
        return next;
    }

//Uses ray tracing to find out whether a point is within a polygon, if the line
//cast passes through twice, it is outside and if it passes once it is within
    public static boolean isInPolygon(Position p, List<Position> poly) {
        for (int i=0; i < poly.size() - 1; i++){
            if (onBorder(poly.get(i), poly.get(i + 1),p)) {
                return true;
            }
        }

        boolean inside = false;
        double x = p.getLng(), y = p.getLat();
        for (int i = 0, j = poly.size() - 1; i < poly.size(); j = i++) {
             double xi = poly.get(i).getLng(), yi = poly.get(i).getLat();
             double xj = poly.get(j).getLng(), yj = poly.get(j).getLat();

             boolean intersect = ((yi > y) !=(yj > y)) &&(x < (xj - xi) * (y - yi)/((yj - yi)) + xi);
             if (intersect)inside = !inside;
        }
        return inside;
    }

//Calculates if a point is on the border as it counts as being inside.
    private static boolean onBorder(Position p, Position q, Position r) {
        double px = p.getLng(), py = p.getLat();
        double qx = q.getLng(), qy = q.getLat();
        double rx = r.getLng(), ry = r.getLat();

        if (orientation(p, q,r) != 0) {
            return false;
        }
        return rx >= Math.min(px, qx) - 0.0 && rx <= Math.max(px, qx) + 0.0 &&
               ry >= Math.min(py, qy) - 0.0 && ry <= Math.max(py, qy) + 0.0;

    }

//Calculates turn direction between three points (clockwise(1), counterclockwise(2), straight line(0))
    private static int orientation(Position p, Position q, Position r) {
        double val = (q.getLat() - p.getLat())*(r.getLng() - q.getLng())
                - (q.getLng() - p.getLng()) * (r.getLat() - q.getLat());

        if (Math.abs(val)< 1e-15) return 0;
        return (val > 0) ? 1 : 2;
    }

    public static boolean pathCrossesRegion(Position start, Position end, List<Position> polygon) {
        double x1 = start.getLng();
        double y1 = start.getLat();
        double x2 = end.getLng();
        double y2 = end.getLat();

        for (int i = 0; i < polygon.size(); i++) {
            Position p1 = polygon.get(i);
            Position p2 = polygon.get((i + 1) % polygon.size());

            double x3 = p1.getLng();
            double y3 = p1.getLat();
            double x4 = p2.getLng();
            double y4 = p2.getLat();

            if(Line2D.linesIntersect(x1, y1, x2, y2, x3, y3, x4, y4)) {
                return true;
            }
        }
        return false;
    }
}
