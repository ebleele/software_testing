package com.ilp2_ella_behan.controller;

import com.ilp2_ella_behan.data.*;
import com.ilp2_ella_behan.service.IlpDroneService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1")
public class DroneController {
    @Autowired
    private IlpDroneService ilpDroneService;

    public DroneController(IlpDroneService ilpDroneService){
        this.ilpDroneService = ilpDroneService;
    }

    @GetMapping("/dronesWithCooling/{state}")
    public List<String> dronesWithCooling(@PathVariable("state") boolean state) {
        return ilpDroneService.getDroneIdsWithCooling(state);
    }

    @GetMapping("/droneDetails/{id}")
    public ResponseEntity<Drone> droneDetails(@PathVariable("id") int id) {
        return ilpDroneService.getDroneById(String.valueOf(id)).map(ResponseEntity::ok)
                .orElseGet(()-> ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }

    @GetMapping("/queryAsPath/{attributeName}/{attributeValue}")
    public List<String> queryAsPath(@PathVariable String attributeName,
                                    @PathVariable String attributeValue) {
        return ilpDroneService.queryAsPath(attributeName, attributeValue);
    }
    @GetMapping("/getRestrictedAreas")
    public List<RestrictedArea> getRestrictedAreas() {
        return ilpDroneService.getRestrictedAreas();
    }

    @PostMapping("/queryAvailableDrones")
    public List<String> queryAvailableDrones(@RequestBody List<MedDispatchRec> dispatches) {
        return ilpDroneService.queryAvailableDrones(dispatches);
    }

    @PostMapping("/query")
    public List<String> query(@RequestBody List<QueryAttribute> queries) {
        return ilpDroneService.query(queries);
    }

    @PostMapping("/calcDeliveryPath")
    public DeliveryPlan calcDeliveryPath(@RequestBody List<MedDispatchRec> dispatches) {
        return ilpDroneService.calcDeliveryPath(dispatches);
    }

    @PostMapping("/calcDeliveryPathAsGeoJson")
    public Map<String,Object> calcDeliveryPathAsGeoJson(@RequestBody List<MedDispatchRec> dispatches) {
        return ilpDroneService.calcDeliveryPathAsGeoJson(dispatches);
    }
}
