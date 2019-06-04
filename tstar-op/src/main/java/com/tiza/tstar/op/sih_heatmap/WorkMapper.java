package com.tiza.tstar.op.sih_heatmap;

import com.tiza.tstar.op.util.DBUtil;
import org.apache.commons.lang3.StringUtils;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;

import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

/**
 * Description: WorkMapper
 * Author: DIYILIU
 * Update: 2019-06-04 09:55
 */
public class WorkMapper extends Mapper<LongWritable, Text, WorkKey, WorkValue> {
    private final static String separator = "\t";
    private List<String> vehicles;

    private int count = 1;

    @Override
    protected void setup(Context context) throws IOException, InterruptedException {
        Configuration conf = context.getConfiguration();
        conf.addResource("op-core.xml");

        vehicles = loadVehicle(conf);
        System.out.println("高速公路热度分析, CQ4[" + vehicles.size() + "] ... ");
    }


    @Override
    protected void map(LongWritable key, Text value, Context context) throws IOException, InterruptedException {
        if (count > 2000) {

            return;
        }
        String str = value.toString();
        if (str != null && str.length() > 0) {
            // 分割单行数据
            String[] arr = value.toString().split(separator);
            if (arr == null || arr.length < 67) {
                return;
            }

            String vehicle = arr[1];
            if (vehicles.contains(vehicle)) {
                Integer location = getInt(arr[7]);
                String enLng = arr[12];
                String enLat = arr[13];
                Integer gpsSpeed = getInt(arr[20]);

                if (location == null || gpsSpeed == null ||
                        location == 0 || gpsSpeed < 60 || gpsSpeed > 200) {
                    return;
                }
                if (StringUtils.isEmpty(enLng) || StringUtils.isEmpty(enLat)) {
                    return;
                }
                count++;
                WorkKey workKey = new WorkKey();
                workKey.setVehicle(vehicle);

                WorkValue workValue = new WorkValue();
                workValue.setName("vehicle:" + vehicle);

                str = location + "," + gpsSpeed + "," + enLng + "," + enLat;
                System.out.println("vehicle:" + vehicle + "[" + str + "]");
                context.write(workKey, workValue);
            }
        }
    }

    private List<String> loadVehicle(Configuration configuration) {
        String sql = "SELECT t.vhcle FROM t_vehicle t WHERE t.device_id IS NOT NULL AND t.zsd_cmc LIKE 'CQ4%'";
        List<String> list = new ArrayList();
        try (Connection connection = DBUtil.getConnection(configuration);
             Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(sql)) {
            while (resultSet.next()) {
                String vehicle = resultSet.getString("vhcle");
                list.add(vehicle);
            }
        } catch (Exception e) {
            e.getMessage();
        }

        return list;
    }

    public static Integer getInt(String value) {
        if (value == null) {
            return null;
        }
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException e) {
            return null;
        }
    }
}
