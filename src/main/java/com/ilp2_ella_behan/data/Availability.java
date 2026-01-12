package com.ilp2_ella_behan.data;

import java.time.LocalTime;

public class Availability {
    private String dayOfWeek;
    private LocalTime from;
    private LocalTime until;

    public String getDayOfWeek() { return dayOfWeek; }
    public void setDayOfWeek(String dayOfWeek) { this.dayOfWeek = dayOfWeek; }

    public LocalTime getFrom() { return from; }
    public void setFrom(LocalTime from) { this.from = from; }

    public LocalTime getUntil() { return until; }
    public void setUntil(LocalTime until) { this.until = until; }
}
