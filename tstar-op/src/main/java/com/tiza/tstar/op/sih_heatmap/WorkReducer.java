package com.tiza.tstar.op.sih_heatmap;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.client.HConnection;
import org.apache.hadoop.hbase.client.HConnectionManager;
import org.apache.hadoop.hbase.client.HTable;
import org.apache.hadoop.hbase.client.Put;
import org.apache.hadoop.hbase.util.Bytes;
import org.apache.hadoop.io.NullWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Reducer;

import java.io.IOException;
import java.util.Iterator;

/**
 * Description: WorkReducer
 * Author: DIYILIU
 * Update: 2019-06-04 09:58
 */
public class WorkReducer extends Reducer<WorkKey, WorkValue, Text, NullWritable> {
    private final static byte[] FAMILY = Bytes.toBytes("1");

    private String jobTime;
    private String geoTable;
    private HConnection conn;

    @Override
    protected void setup(Context context) throws IOException, InterruptedException {
        Configuration conf = context.getConfiguration();
        jobTime = conf.get("jobTime");
        geoTable = "tstar:SIH_HIGHWAY_HEATMAP";

        org.apache.hadoop.conf.Configuration hconf = HBaseConfiguration.create();
        //配置参数，第一个是主机名，第二个是端口号
        hconf.set("hbase.zookeeper.quorum", "scsp00261.saicdt.com,scsp00262.saicdt.com,scsp00266.saicdt.com");
        conn = HConnectionManager.createConnection(hconf);
    }

    @Override
    protected void reduce(WorkKey key, Iterable<WorkValue> values, Context context) throws IOException, InterruptedException {
        int count = 0;
        for (Iterator<WorkValue> iterator = values.iterator(); iterator.hasNext(); ) {
            WorkValue v = iterator.next();
            count++;
            System.out.println(v.getName());
        }
        System.out.println("高速热度分析结果: [GEOHASH: " + key.getGeohash() + ", 重复率: " + count + "]");
        toHBase(key, count);
    }


    public void toHBase(WorkKey key, int count) throws IOException {
        if (conn == null) {
            System.out.println("HConnection is null.");
            return;
        }

        HTable table = (HTable) conn.getTable(geoTable);
        byte[] rowKeys = Bytes.toBytes(key.getGeohash());
        Put put = new Put(rowKeys);
        put.add(FAMILY, Bytes.toBytes("lng"), Bytes.toBytes(key.getLng()));
        put.add(FAMILY, Bytes.toBytes("lat"), Bytes.toBytes(key.getLat()));
        put.add(FAMILY, Bytes.toBytes(Integer.valueOf(jobTime)), Bytes.toBytes(count));
        table.put(put);
    }
}
