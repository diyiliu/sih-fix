package com.tiza.report.model;

import lombok.Data;

/**
 * Description: WorkValue
 * Author: DIYILIU
 * Update: 2019-04-04 15:33
 */

@Data
public class WorkValue implements Comparable<WorkValue> {

    public WorkValue() {

    }

    public WorkValue(String vhcle, long time, String speed, String mileage, String fuelConsumption) {
        this.vhcle = vhcle;
        this.time = time;
        this.speed = speed;
        this.mileage = mileage;
        this.fuelConsumption = fuelConsumption;
    }

    private String vhcle;

    private Long time;

    private String speed;

    private String mileage;

    private String fuelConsumption;

    @Override
    public int compareTo(WorkValue o) {

        return time.compareTo(o.time);
    }
}
