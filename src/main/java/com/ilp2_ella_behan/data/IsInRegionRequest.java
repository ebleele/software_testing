package com.ilp2_ella_behan.data;

public class IsInRegionRequest {
    private Position position;
    private Region region;
    private String name;

    public Position getPosition() {
        return position;
    }
    public void setPosition(Position position){
        this.position = position;
    }
    public Region getRegion() {
        return region;
    }
    public void setRegion(Region region){
        this.region = region;
    }

    public String getName() {
        return name;
    }
    public void setName(String name){
        this.name = name;
    }

}
