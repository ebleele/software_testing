package com.ilp2_ella_behan.data;

import java.util.List;

public class RestrictedArea {
    public String name;
    public Integer id;
    public Limits limits;
    public List<Position> vertices;

    public RestrictedArea(){}
    public RestrictedArea(String name, Integer id, Limits limits, List<Position> vertices){
        this.name = name;
        this.id = id;
        this.limits = limits;
        this.vertices = vertices;
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
    public Limits getLimits(){
        return limits;
    }
    public void setLimits(Limits limits) {
        this.limits = limits;
    }
    public List<Position> getVertices(){
        return vertices;
    }
    public void setVertices(List<Position> vertices) {
        this.vertices = vertices;
    }
}
