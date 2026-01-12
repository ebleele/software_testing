package com.ilp2_ella_behan.data;

import java.util.List;

public class DeliveryPlan {
    private double totalCost;
    private int totalMoves;
    private List<DronePath> dronePaths;

    public double getTotalCost() {
        return totalCost;
    }

    public void setTotalCost(double totalCost) {
        this.totalCost = totalCost;
    }

    public int getTotalMoves() {
        return totalMoves;
    }

    public void setTotalMoves(int totalMoves) {
        this.totalMoves = totalMoves;
    }

    public List<DronePath> getDronePaths() {
        return dronePaths;
    }

    public void setDronePaths(List<DronePath> dronePaths) {
        this.dronePaths = dronePaths;
    }
}
