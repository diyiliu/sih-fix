package com.tiza.location.util;

import com.tiza.plugin.model.Position;
import lombok.extern.slf4j.Slf4j;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.*;
import org.apache.hadoop.hbase.util.Bytes;

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

    public Position fetchPosition(String id, long sTime, long eTime) throws Exception {
        //log.info("查询 HBase 参数: [{}, {}]", id, DateUtil.dateToString(new Date(time)));

        byte[] startRow = build(id, eTime);
        byte[] endRow = build(id, sTime);
        byte[] colBytes = Bytes.toBytes(0x200);

        Position position = null;
        try (Connection connection = ConnectionFactory.createConnection(config)) {
            Table table = connection.getTable(TableName.valueOf(tableName));

            byte[] family = Bytes.toBytes(CF_DEFAULT);
            Scan scan = new Scan(startRow, endRow);
            scan.addColumn(family, colBytes);

            try (ResultScanner rs = table.getScanner(scan)) {
                for (Result r = rs.next(); r != null; r = rs.next()) {
                    if (r.containsNonEmptyColumn(family, colBytes)) {
                        String str = Bytes.toString(r.getValue(family, colBytes));
                        String[] dataArr = str.split("\t");

                        Double enLng = Double.valueOf(dataArr[12]);
                        Double enLat = Double.valueOf(dataArr[13]);
                        if (enLng != null && enLat != null) {
                            String gpsDate = dataArr[5];

                            position = new Position();
                            position.setGpsDate(gpsDate);
                            position.setEnLng(enLng);
                            position.setEnLat(enLat);

                            break;
                        }
                    }
                }
            }
        }

        return position;
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
}
