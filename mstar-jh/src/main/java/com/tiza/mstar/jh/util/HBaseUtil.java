package com.tiza.mstar.jh.util;

import com.tiza.mstar.jh.model.CanBody;
import com.tiza.plugin.util.CommonUtil;
import com.tiza.plugin.util.JacksonUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.Cell;
import org.apache.hadoop.hbase.CellUtil;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.*;
import org.apache.hadoop.hbase.util.Bytes;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


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

    public List<CanBody> fetchData(String id, long sTime, long eTime) throws Exception {
        //log.info("查询 HBase 参数: [{}, {}]", id, DateUtil.dateToString(new Date(time)));
        byte[] startRow = build(id, eTime);
        byte[] endRow = build(id, sTime);
        byte[] colBytes = Bytes.toBytes(0x87);

        List list = new ArrayList();
        try (Connection connection = ConnectionFactory.createConnection(config)) {
            Table table = connection.getTable(TableName.valueOf(tableName));
            byte[] family = Bytes.toBytes(CF_DEFAULT);
            Scan scan = new Scan(startRow, endRow);
            scan.addColumn(family, colBytes);

            try (ResultScanner rs = table.getScanner(scan)) {
                for (Result r = rs.next(); r != null; r = rs.next()) {
                    if (r.containsNonEmptyColumn(family, colBytes)) {
                        for (Cell cell : r.rawCells()) {
                            byte[] keyArr = CellUtil.cloneRow(cell);
                            byte[] timeArr = new byte[8];
                            System.arraycopy(keyArr, keyArr.length - 8, timeArr, 0, 8);
                            long time = Long.MAX_VALUE - CommonUtil.bytesToLong(timeArr);

                            byte[] valArr = CellUtil.cloneValue(cell);
                            String value = new String(valArr);

                            Map map = JacksonUtil.toObject(value, HashMap.class);
                            if (map.containsKey("can")) {
                                String canJson = JacksonUtil.toJson(map.get("can"));
                                CanBody canBody = JacksonUtil.toObject(canJson, CanBody.class);
                                if (canBody.getMileage() == null || canBody.getOil() == null ||
                                        canBody.getPump() == null || canBody.getPumpTime() == null) {

                                    continue;
                                }
                                canBody.setId(id);
                                canBody.setTime(time);
                                list.add(canBody);
                            }
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
}
