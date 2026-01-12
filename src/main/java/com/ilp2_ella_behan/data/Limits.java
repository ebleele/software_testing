package com.ilp2_ella_behan.data;

public class Limits {
    public Integer lower;
    public Integer upper;

    public Limits(){}
    public Limits(Integer lower, Integer upper){
        this.lower = lower;
        this.upper = upper;
    }

    public Integer getLower(){
        return lower;
    }
    public void setLower(Integer lower) {
        this.lower = lower;
    }
    public Integer getUpper(){
        return upper;
    }
    public void setUpper(Integer upper) {
        this.upper = upper;
    }
}
