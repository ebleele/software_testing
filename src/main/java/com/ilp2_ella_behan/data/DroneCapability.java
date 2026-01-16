package com.ilp2_ella_behan.data;

public class DroneCapability {
    public Boolean cooling;
    public Boolean heating;
    public Double capacity;
    public Integer maxMoves;
    public Double costPerMove;
    public Double costInitial;
    public Double costFinal;

    public DroneCapability(){}
    public DroneCapability(Boolean cooling, Boolean heating, Double capacity,
                           Integer maxMoves, Double costPerMove,
                           Double costInitial, Double costFinal) {
        this.cooling = cooling;
        this.heating = heating;
        this.capacity = capacity;
        this.maxMoves = maxMoves;
        this.costPerMove = costPerMove;
        this.costInitial = costInitial;
        this.costFinal = costFinal;
    }

    public Boolean getCooling(){
        return cooling;
    }
    public void setCooling(boolean cooling) {
        this.cooling = this.cooling;
    }
    public Boolean getHeating(){
        return heating;
    }
    public void setHeating(boolean heating) {
        this.heating = this.heating;
    }
    public Double getCapacity(){
        return capacity;
    }
    public void setCapacity() {
        this.capacity = capacity;
    }
    public Integer getMaxMoves(){
        return maxMoves;
    }
    public void setMaxMoves(int i) {
        this.maxMoves = maxMoves;
    }
    public Double getCostPerMove(){
        return costPerMove;
    }
    public void setCostPerMove(double v) {
        this.costPerMove = costPerMove;
    }
    public Double getCostInitial(){
        return costInitial;
    }
    public void setCostInitial(double v) {
        this.costInitial = costInitial;
    }
    public Double getCostFinal(){
        return costFinal;
    }
    public void setCostFinal(double v) {
        this.costFinal = costFinal;
    }

}
