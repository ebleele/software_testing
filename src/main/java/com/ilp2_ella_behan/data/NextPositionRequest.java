package com.ilp2_ella_behan.data;

public class NextPositionRequest {
    private Position start;
    private Double angle;
    public Position getStart() {
        return start;
    }
    public void setStart(Position start) {
        this.start = start;
    }

    public Double getAngle() {
        return angle;
    }
    public void setAngle(Double angle) {
        this.angle = angle;
    }
}
