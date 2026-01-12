package com.ilp2_ella_behan.data;

import java.time.LocalDate;
import java.time.LocalTime;

public class MedDispatchRec {
    public Integer id;
    public LocalDate date;
    public LocalTime time;
    public Requirements requirements;
    public Position delivery;


    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public LocalTime getTime() {
        return time;
    }

    public void setTime(LocalTime time) {
        this.time = time;
    }

    public Requirements getRequirements() {
        return requirements;
    }

    public void setRequirements(Requirements requirements) {
        this.requirements = requirements;
    }

    public Position getDelivery() {
        return delivery;
    }

    public void setDelivery(Position delivery) {
        this.delivery = delivery;
    }

}
