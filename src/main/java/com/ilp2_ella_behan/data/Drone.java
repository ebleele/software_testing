package com.ilp2_ella_behan.data;

public class Drone {
    public String name;
    public String id;
    public DroneCapability capability;

    public Drone() {}

    public Drone(String name, String id, DroneCapability capability) {
        this.name = name;
        this.id = id;
        this.capability = capability;
    }

    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public String getId() {
        return id;
    }
    public void setId(String id) {
        this.id = id;
    }
    public DroneCapability getCapability() {
        return capability;
    }
    public void setCapability(DroneCapability capability) {
        this.capability = capability;
    }


}
