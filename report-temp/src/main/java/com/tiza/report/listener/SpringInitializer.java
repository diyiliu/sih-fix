package com.tiza.report.listener;

import com.tiza.plugin.util.CommonUtil;
import com.tiza.plugin.util.DateUtil;
import com.tiza.report.model.RecordData;
import com.tiza.report.model.WorkValue;
import com.tiza.report.util.HBaseUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;

/**
 * Description: SpringInitializer
 * Author: DIYILIU
 * Update: 2019-01-30 14:26
 */

@Slf4j
@Component
public class SpringInitializer implements ApplicationListener {

    @Resource
    private JdbcTemplate jdbcTemplate;

    @Resource
    private HBaseUtil hbaseUtil;

    @Value("${vehicles}")
    private String vehicles;

    private final int online_times = 300;

    private final int fuel_threshold = 100;

    private final int mil_threshold = 100;

    @Override
    public void onApplicationEvent(ApplicationEvent event) {
        String[] ids = vehicles.split(",");
        log.info("=================== 运输扫描修复数据总数: [{}] ===================", ids.length);
        String day = "2019-03-26";
        int count = 0;
        for (String id : ids) {
            start(day, 7, id);
            count++;
        }
        log.info("=================== 运输扫描修复位置数据完成, 数量[{}]。 ===================", count);
    }

    private void start(String day, int count, String vehicle) {
        String str = day + " 00:00:00";
        Date date = DateUtil.stringToDate(str);

        for (int i = 0; i < count; i++) {
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(date);
            calendar.add(Calendar.DAY_OF_MONTH, 1);

            Date end = calendar.getTime();
            try {
                List<WorkValue> list = hbaseUtil.fetchData(vehicle, date.getTime(), end.getTime());
                if (CollectionUtils.isEmpty(list)) {
                    continue;
                }
                // 按时间排序
                Collections.sort(list);
                handle(vehicle, list);
            } catch (Exception e) {
                e.printStackTrace();
            }
            date = end;
        }
    }


    private void handle(String id, List<WorkValue> list) {
        long beforeGpsTime = 0;
        int beforeStatus = 0;
        double beforeMileage = 0.0;
        double beforeFuelConsumption = 0.0;

        int onlineTime = 0;
        int stopTime = 0;
        double dayMileage = 0.0;
        double dayFuelConsumption = 0.0;

        double totalMileage = 0.0;
        double totalFuelConsumption = 0.0;

        for (WorkValue workValue : list) {
            long gpsTime = workValue.getTime();
            int status = Double.parseDouble(workValue.getSpeed()) < 1 ? 1 : 2;// 1:停留

            // 2:行驶
            double mileage = Double.parseDouble(workValue.getMileage());
            double fuelConsumption = Double.parseDouble(workValue.getFuelConsumption());
            if (mileage > 99999999) {
                continue;
            }else {
                totalMileage = mileage;
            }

            // 总油耗 展示当天最大有效值
            if (fuelConsumption > totalFuelConsumption) {
                totalFuelConsumption = fuelConsumption;
            }

            // 新时间点 小于 旧时间点 此条新时间点记录不分析
            if (gpsTime < beforeGpsTime) {
                continue;
            }

            // 两点间（新-旧）里程差/时间差所得速度>2000km/h
            if (point2compare(mileage, beforeMileage, gpsTime, beforeGpsTime)) {
                continue;
            }

            double tmpMil = mileage - beforeMileage;
            double tmpFuel = fuelConsumption - beforeFuelConsumption;

            if (tmpMil >= 0 && tmpMil <= mil_threshold) {
                dayMileage += tmpMil;
            }

            // 两点之间的油耗差值需要>=50L不参与计算。 xml 文件中已经修改了阀值 50
            if (tmpFuel >= 0 && tmpFuel <= fuel_threshold) {
                dayFuelConsumption += tmpFuel;
            }

            if ((gpsTime - beforeGpsTime) <= online_times * 1000) {
                // 在线
                onlineTime += (gpsTime - beforeGpsTime);
                if (beforeStatus == status && status == 1) {
                    stopTime += gpsTime - beforeGpsTime;
                }
            }

            beforeGpsTime = gpsTime;
            beforeStatus = status;
            beforeMileage = mileage;
            beforeFuelConsumption = fuelConsumption;
        }

        if (dayMileage >= 0 && dayFuelConsumption >= 0) {
            double lp100km = 0.0;
            if (dayMileage > 0) {
                lp100km = CommonUtil.keepDecimal((dayFuelConsumption / dayMileage) * 100.0, 2);
            }

            String dayStr = DateUtil.dateToString(new Date(beforeGpsTime), "%1$tY-%1$tm-%1$td");
            Date day = DateUtil.stringToDate(dayStr + " 00:00:00");

            RecordData recordData = new RecordData(id, day,
                    CommonUtil.keepDecimal(dayFuelConsumption, 2),
                    CommonUtil.keepDecimal(dayMileage, 2),
                    CommonUtil.keepDecimal(totalFuelConsumption, 2),
                    CommonUtil.keepDecimal(totalMileage, 2), lp100km, onlineTime / 1000,
                    stopTime / 1000);

            dealDb(recordData);
        }
    }

    private void dealDb(RecordData data) {
        String sql = "INSERT INTO z_stat_vehicle_daily (`vhcle`, `date`, `day_fuel_consumption`,`day_mileage`,`total_fuel_consumption`,`total_mileage`,`lp100km`,`online_time`,`stop_time`)" +
                "VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?)";

        Object[] params = new Object[]{data.getVhcle(), data.getDate(), data.getDayFuelConsumption(), data.getDayMileage(), data.getTotalFuelConsumption(), data.getTotalMileage(),
                data.getLp100km(), data.getOnlineTime(), data.getStopTime()};
        jdbcTemplate.update(sql, params);
    }

    private boolean point2compare(double mileage, double beforeMileage, long gpsTime, long beforeGpsTime) {

        long time_value = gpsTime - beforeGpsTime;
        double hours_value = (double) time_value / (double) (60 * 60 * 1000);
        double mileage_value = mileage - beforeMileage;
        if (mileage_value / hours_value > 2000) {
            return true;
        }
        return false;
    }
}
