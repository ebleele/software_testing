package com.ilp2_ella_behan.data;

import com.ilp2_ella_behan.data.Availability;

import java.util.List;

public class DroneAvailabilityItem {
    private String id;
    private List<Availability> availability;

    public String getId(){
        return id;
    }
    public void setId(String id) {
        this.id = id;
    }
    public List<Availability> getAvailability(){
        return availability;
    }
    public void setAvailability(List<Availability> availability) {
        this.availability = availability;
    }
}
