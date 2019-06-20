package com.tiza.mstar.jh.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/**
 * Description: CanBody
 * Author: DIYILIU
 * Update: 2019-06-18 09:55
 */

@Data
public class CanBody implements Comparable<CanBody> {

    private String id;

    private Long time;

    /* 总里程 */
    @JsonProperty("DECIMAL41")
    private Double mileage;

    /* 泵送时间 */
    @JsonProperty("DECIMAL42")
    private Double pumpTime;

    /* 总油耗 */
    @JsonProperty("DECIMAL43")
    private Double oil;

    /* 泵送量 */
    @JsonProperty("DECIMAL44")
    private Double pump;

    @JsonProperty("BIT1")
    private Double engineStop;

    @JsonProperty("BIT2")
    private Double unsetPan;

    @JsonProperty("BIT3")
    private Double overTemp;

    @JsonProperty("BIT4")
    private Double overPress;

    @JsonProperty("BIT5")
    private Double remoteController;

    @JsonProperty("BIT6")
    private Double encoder;

    @JsonProperty("BIT7")
    private Double display;

    @JsonProperty("BIT8")
    private Double pan;

    @JsonProperty("BIT9")
    private Double controller;

    @JsonProperty("BIT10")
    private Double lowTemp;

    @JsonProperty("BIT11")
    private Double oilPump;

    @JsonProperty("BIT12")
    private Double turn;

    @JsonProperty("BIT13")
    private Double liquidLevel;

    @JsonProperty("BIT14")
    private Double transfer;

    @Override
    public int compareTo(CanBody o) {
        return time.compareTo(o.getTime());
    }
}
