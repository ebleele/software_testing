package com.ilp2_ella_behan;

import com.ilp2_ella_behan.data.DistanceToRequest;
import com.ilp2_ella_behan.data.IsInRegionRequest;
import com.ilp2_ella_behan.data.NextPositionRequest;
import com.ilp2_ella_behan.data.Position;
import com.ilp2_ella_behan.service.Geometry;
import com.ilp2_ella_behan.service.RequestValidator;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import static com.ilp2_ella_behan.service.RequestValidator.round;

@RestController
@RequestMapping("/api/v1")
public class Controller {
    private final RequestValidator validator;
    private final Geometry geometry;

    public Controller(RequestValidator validator, Geometry geometry) {
        this.validator = validator;
        this.geometry = geometry;
    }

    @GetMapping("/uid")
    public String uid(){
        return "s2527016";
    }

    @PostMapping("/distanceTo")
    public ResponseEntity<Double> distanceTo(@RequestBody DistanceToRequest req) {
        if (validator.acceptableRequest(req) && validator.acceptablePosition(req.getPosition1())
                &&validator.acceptablePosition(req.getPosition2())){
            return new ResponseEntity<>(round(geometry.distance(req.getPosition1(),
                    req.getPosition2())),HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }

    @PostMapping("/isCloseTo")
    public ResponseEntity<Boolean> isCloseTo(@RequestBody DistanceToRequest req){
        if (validator.acceptableRequest(req) && validator.acceptablePosition(req.getPosition1())
                &&validator.acceptablePosition(req.getPosition2())){
            return new ResponseEntity<>((Geometry.isCloseTo(req.getPosition1(), req.getPosition2())),
                    HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }

    @PostMapping("/nextPosition")
    public ResponseEntity<Position> nextPosition(@RequestBody NextPositionRequest req){
        if (validator.acceptableRequest(req)){
            return new ResponseEntity<>((geometry.nextPosition(req.getStart(),
                    (req.getAngle()))),HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }

    @PostMapping("/isInRegion")
    public ResponseEntity<Boolean> isInRegion(@RequestBody IsInRegionRequest req) {
        if (validator.acceptableRequest(req)){
            return new ResponseEntity<>((geometry.isInPolygon(req.getPosition(),
                    req.getRegion().getVertices())),HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }
    }


