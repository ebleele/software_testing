package com.ilp2_ella_behan.service;

import com.ilp2_ella_behan.data.Position;
import com.ilp2_ella_behan.data.Region;
import com.ilp2_ella_behan.data.DistanceToRequest;
import com.ilp2_ella_behan.data.IsInRegionRequest;
import com.ilp2_ella_behan.data.NextPositionRequest;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Objects;

@Service
public class RequestValidator {
    //acceptableRequest checks there is no missing or invalid data for all requests
    public boolean acceptableRequest(DistanceToRequest req) {
        return req != null && req.getPosition1() != null && req.getPosition2() != null;
    }

    public boolean acceptablePosition(Position position) {
        if (position == null) return false;
        if (position.getLat() == null | position.getLng() == null) return false;
        if (position.getLat() > 90 | position.getLat() < -90) return false;
        return !(position.getLng() > 180 | position.getLng() < -180);

    }
    //Angles must be a multiple of 22.5 as per spec
    public boolean acceptableRequest(NextPositionRequest req) {
        if (req == null) return false;
        if (!acceptablePosition((req.getStart()))) return false;
        Double angle =req.getAngle();
        if (angle>360 | angle<0) return false;
        return angle % 22.5 == 0.0;
    }

    //For it to be a polygon it must have 3 or more edges and no missing data
    public boolean acceptableRequest(IsInRegionRequest req) {
        if (req == null || req.getPosition() == null){
            return false;
        }
        Region r = req.getRegion();
        if (r == null || r.getVertices() == null|| r.getName() == null){
            return false;
        }
        List<Position> v = r.getVertices();
        if (v.size() < 4){
            return false;
        }

        //Make sure it is a closed shape
        Position first = v.getFirst();
        Position last = v.getLast();
        return first !=null && last !=null
                && Objects.equals(first.getLng(), last.getLng())
                && Objects.equals(first.getLat(), last.getLat());
    }

    //Helper method used for rounding to 6 sig fig.
    public static double round(double value) {
        int decimalPlaces = 6;
        BigDecimal bd = new BigDecimal(Double.toString(value));
        bd = bd.setScale(decimalPlaces, RoundingMode.HALF_UP);
        return bd.doubleValue();
    }

}
