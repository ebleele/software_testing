package com.ilp2_ella_behan.data;

import java.util.List;

public class DeliveryPath {
    private Integer deliveryId;
    private List<Position> flightPath;

    public Integer getDeliveryId() {
        return deliveryId;
    }

    public void setDeliveryId(Integer deliveryId) {
        this.deliveryId = deliveryId;
    }

    public List<Position> getFlightPath() {
        return flightPath;
    }

    public void setFlightPath(List<Position> flightPath) {
        this.flightPath = flightPath;
    }
}
