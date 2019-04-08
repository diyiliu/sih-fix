package com.tiza.report.util;

import com.tiza.plugin.util.DateUtil;
import com.tiza.plugin.util.JacksonUtil;
import com.tiza.report.model.WorkValue;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.*;
import org.apache.hadoop.hbase.util.Bytes;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;


/**
 * Description: HBaseUtil
 * Author: DIYILIU
 * Update: 2019-01-30 10:57
 */
@Slf4j
public class HBaseUtil {
    private final static String CF_DEFAULT = "1";

    private Configuration config;
    private String tableName;

    public List<WorkValue> fetchData(String id, long sTime, long eTime) throws Exception {
        //log.info("查询 HBase 参数: [{}, {}]", id, DateUtil.dateToString(new Date(time)));
        byte[] startRow = build(id, eTime);
        byte[] endRow = build(id, sTime);
        byte[] colBytes = Bytes.toBytes(0x200);

        List<WorkValue> list = new ArrayList();
        try (Connection connection = ConnectionFactory.createConnection(config)) {
            Table table = connection.getTable(TableName.valueOf(tableName));
            byte[] family = Bytes.toBytes(CF_DEFAULT);
            Scan scan = new Scan(startRow, endRow);
            scan.addColumn(family, colBytes);

            try (ResultScanner rs = table.getScanner(scan)) {
                for (Result r = rs.next(); r != null; r = rs.next()) {
                    if (r.containsNonEmptyColumn(family, colBytes)) {
                        String str = Bytes.toString(r.getValue(family, colBytes));
                        String arr[] = str.split("\t");

                        if (arr != null && arr.length > 66) {
                            String gpsTime = arr[5];
                            String createTime = arr[6];
                            String speed = arr[21];// 速度
                            String mileage = arr[22];// 总里程
                            String fuelConsumption = arr[31];// 总油耗
                            String acc = arr[49]; // acc状态开关

                            if (StringUtils.isEmpty(mileage) ||
                                    StringUtils.isEmpty(speed) ||
                                    StringUtils.isEmpty(gpsTime) ||
                                    StringUtils.isEmpty(createTime) ||
                                    !"1".equals(acc)) {

                                continue;
                            }

                            if (isCompensate(DateUtil.stringToDate(gpsTime), DateUtil.stringToDate(createTime))) {
                                continue;
                            }

                            // 总油耗 会不传
                            if (StringUtils.isEmpty(fuelConsumption)) {
                                fuelConsumption = "0";
                            }

                            WorkValue workValue = new WorkValue(id, DateUtil.stringToDate(gpsTime).getTime(), speed, mileage, fuelConsumption);
                            list.add(workValue);
                        }
                    }
                }
            }
        }

        return list;
    }

    /**
     * 生成 rowKey
     *
     * @param id
     * @param time
     * @return
     */
    public static byte[] build(String id, long time) {
        byte[] prefix = Bytes.toBytes((short) (Math.abs(id.hashCode() % 32768)));
        prefix = Bytes.add(prefix, Bytes.toBytes(id));

        return Bytes.add(prefix, new byte[]{0}, Bytes.toBytes(Long.MAX_VALUE - time));
    }

    public void setConfig(Configuration config) {
        this.config = config;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    private boolean isCompensate(Date d1, Date d2) {
        long value = Math.abs(d1.getTime() - d2.getTime());
        if (value > 5 * 60 * 1000) {
            return true;
        }
        return false;
    }
}
