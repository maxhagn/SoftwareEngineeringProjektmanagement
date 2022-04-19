package at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.events;

import java.util.Date;

public class PerformanceDto {
    private int id;
    private Date beginDate;
    private int duration;
    private float price;
    private String hall;

    public int getId() {
        return id;
    }

    public Date getBeginDate() {
        return beginDate;
    }

    public int getDuration() {
        return duration;
    }

    public float getPrice() {
        return price;
    }

    public String getHall() {
        return hall;
    }
}
