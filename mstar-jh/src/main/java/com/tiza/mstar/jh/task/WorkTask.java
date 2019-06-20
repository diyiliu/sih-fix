package com.tiza.mstar.jh.task;

import com.tiza.mstar.jh.model.CanBody;
import com.tiza.mstar.jh.util.HBaseUtil;
import com.tiza.plugin.model.facade.ITask;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.annotation.Resource;
import java.util.Collections;
import java.util.Date;
import java.util.List;

/**
 * Description: WorkTask
 * Author: DIYILIU
 * Update: 2019-06-18 14:14
 */

@Slf4j
public class WorkTask implements ITask {

    @Resource
    private JdbcTemplate jdbcTemplate;

    @Resource
    private HBaseUtil hbaseUtil;


    @Override
    public void execute() {
        log.info("九合每日数据分析 ... ");
        String sql = "SELECT c.id" +
                "  FROM bs_vehicle c" +
                " INNER JOIN bs_terminal t" +
                "    ON t. id = c. terminalid" +
                "   AND t.versioncode = 'QDJH'";

        List<Integer> list = jdbcTemplate.queryForList(sql, Integer.class);

        DateTime dtNow = new DateTime();
        DateTime eTime = new DateTime().withDayOfYear(dtNow.getDayOfYear()).withMillisOfDay(0);
        DateTime sTime = eTime.minusDays(1);

        Date day = sTime.toDate();
        try {
            for (Integer id : list) {
                List<CanBody> canBodyList = hbaseUtil.fetchData(String.valueOf(id), sTime.getMillis(), eTime.getMillis());
                if (CollectionUtils.isEmpty(canBodyList)) {
                    continue;
                }
                // 按时间排序
                Collections.sort(canBodyList);

                // 工况数据
                sql = "INSERT INTO ALY_QDJH_DAILY_MILEAGE(VEHICLEID, RECODEDATE, TOTALMILEAGE, MILEAGES, CREATETIME)VALUES (" + id + ",?,?,?,?)";
                dailyWork(day, sql, canBodyList, "mileage", 100);

                sql = "INSERT INTO ALY_QDJH_DAILY_OIL(VEHICLEID, RECODEDATE, TOTALOIL, OIL, CREATETIME)VALUES (" + id + ",?,?,?,?)";
                dailyWork(day, sql, canBodyList, "oil", 100);

                sql = "INSERT INTO ALY_QDJH_DAILY_PUMP(VEHICLEID, RECODEDATE, TOTALPUMP, PUMP, CREATETIME)VALUES (" + id + ",?,?,?,?)";
                dailyWork(day, sql, canBodyList, "pump", 100);

                sql = "INSERT INTO ALY_QDJH_DAILY_PUMPTIME(VEHICLEID, RECODEDATE, TOTALPUMPTIME, PUMPTIME, CREATETIME)VALUES (" + id + ",?,?,?,?)";
                dailyWork(day, sql, canBodyList, "pumpTime", 100);

                // 报警数据
                sql = "INSERT INTO ALY_QDJH_DAILY_ALARM(VEHICLEID, DATETIME, ALARMTYPE, TIMES, CREATETIME)VALUES(?,?,?,?,?)";
                String[] items = new String[]{"engineStop", "unsetPan", "overTemp", "overPress", "remoteController", "encoder", "display", "pan", "controller", "lowTemp", "oilPump", "turn", "liquidLevel", "transfer"};
                Integer[] types = new Integer[]{11, 12, 13, 14, 15, 16, 17, 18, 21, 22, 23, 24, 25, 26};

                for (int i = 0; i < items.length; i++) {
                    String name = items[i];
                    int type = types[i];
                    int count = dailyAlarm(canBodyList, name);
                    if (count > 0) {
                        log.info("每日报警 =={}==数据", name);
                        jdbcTemplate.update(sql, new Object[]{id, day, type, count, new Date()});
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 每日报警次数统计
     *
     * @param list
     * @param name
     * @return
     * @throws Exception
     */
    public int dailyAlarm(List<CanBody> list, String name) throws Exception {
        int i = 0;
        for (CanBody can : list) {
            String value = BeanUtils.getSimpleProperty(can, name);
            if (StringUtils.isEmpty(value)) {
                continue;
            }
            double d = Double.parseDouble(value);
            if (d > 0) {
                i++;
            }
        }

        return i;
    }

    /**
     * 每日工况数据统计
     *
     * @param day
     * @param sql
     * @param list
     * @param name
     * @param threshold
     * @throws Exception
     */
    public void dailyWork(Date day, String sql, List<CanBody> list, String name, int threshold) throws Exception {
        CanBody can = list.get(0);
        String v = BeanUtils.getSimpleProperty(can, name);
        Double first = Double.parseDouble(v);

        double total = first;
        double before = first;
        double daily = 0;
        for (int i = 1; i < list.size(); i++) {
            can = list.get(i);
            String value = BeanUtils.getSimpleProperty(can, name);
            if (StringUtils.isEmpty(value)) {
                continue;
            }

            double data = Double.parseDouble(value);
            if (data > 99999999) {
                continue;
            }
            total = data;
            double temp = data - before;
            if (temp > 0 && temp < threshold) {
                daily += temp;
            }
            before = data;
        }
        // 写入数据库
        log.info("每日工况 =={}== 数据", name);
        jdbcTemplate.update(sql, new Object[]{day, total, daily, new Date()});
    }
}
