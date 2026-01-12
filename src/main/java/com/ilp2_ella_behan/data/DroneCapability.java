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
    public void setCooling() {
        this.cooling = cooling;
    }
    public Boolean getHeating(){
        return heating;
    }
    public void setHeating() {
        this.heating = heating;
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
    public void setMaxMoves() {
        this.maxMoves = maxMoves;
    }
    public Double getCostPerMove(){
        return costPerMove;
    }
    public void setCostPerMove() {
        this.costPerMove = costPerMove;
    }
    public Double getCostInitial(){
        return costInitial;
    }
    public void setCostInitial() {
        this.costInitial = costInitial;
    }
    public Double getCostFinal(){
        return costFinal;
    }
    public void setCostFinal() {
        this.costFinal = costFinal;
    }

}
