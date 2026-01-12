package com.ilp2_ella_behan.data;

import java.util.List;

public class DronePath {
    private String droneId;
    private List<DeliveryPath> deliveries;

    public String getDroneId() {
        return droneId;
    }

    public void setDroneId(String droneId) {
        this.droneId = droneId;
    }

    public List<DeliveryPath> getDeliveries() {
        return deliveries;
    }

    public void setDeliveries(List<DeliveryPath> deliveries) {
        this.deliveries = deliveries;
    }
}
