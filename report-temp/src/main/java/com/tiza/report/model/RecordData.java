package com.tiza.report.model;

import lombok.Data;

import java.util.Date;

/**
 * Description: RecordData
 * Author: DIYILIU
 * Update: 2019-04-04 15:33
 */

@Data
public class RecordData {

    public RecordData() {

    }

    public RecordData(String vhcle, Date date, double dayFuelConsumption, double dayMileage, double totalFuelConsumption, double totalMileage, double lp100km, int onlineTime, int stopTime) {
        this.vhcle = vhcle;
        this.date = date;
        this.dayFuelConsumption = dayFuelConsumption;
        this.dayMileage = dayMileage;
        this.totalFuelConsumption = totalFuelConsumption;
        this.totalMileage = totalMileage;
        this.lp100km = lp100km;
        this.onlineTime = onlineTime;
        this.stopTime = stopTime;
    }

    private String vhcle;//车辆内部编号
    private Date date;//年月日（YYYYMMDD）
    private double dayFuelConsumption;//油耗(L) ：当日当车总油耗
    private double dayMileage;//里程(KM) ：当日当车总里程
    private double totalFuelConsumption;//总油耗(L) ：该车行驶开始到查询日期总油耗
    private double totalMileage;//总里程(KM) ：该日期车辆总里程（仪表盘里程）
    private double lp100km;//当日当车百公里油耗
    private int onlineTime;//当日当车在线时间
    private int stopTime;//当日当车停留时间
}
