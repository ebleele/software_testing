package com.ilp2_ella_behan.data;

import com.ilp2_ella_behan.data.DroneAvailabilityItem;

import java.util.List;

public class DroneForServicePoint {
    private Integer servicePointId;
    private List<DroneAvailabilityItem> drones;

    public Integer getServicePointId(){
        return servicePointId;
    }
    public void setServicePointId(Integer servicePointId) {
        this.servicePointId = servicePointId;
    }
    public List<DroneAvailabilityItem> getDrones(){
        return drones;
    }
    public void setDrones(List<DroneAvailabilityItem> drones) {
        this.drones = drones;
    }
}
