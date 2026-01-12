package com.ilp2_ella_behan.data;

public class ServicePoint {
    public String name;
    public Integer id;
    public Position location;

    public ServicePoint(){}
    public ServicePoint(String name, Integer id, Position location){
        this.name = name;
        this.id = id;
        this.location = location;
    }

    public String getName(){
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public Integer getId(){
        return id;
    }
    public void setId(Integer id) {
        this.id = id;
    }
    public Position getLocation(){
        return location;
    }
    public void setLocation(Position location) {
        this.location = location;
    }


}
