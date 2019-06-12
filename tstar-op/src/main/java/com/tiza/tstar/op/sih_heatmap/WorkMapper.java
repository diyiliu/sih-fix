package com.tiza.tstar.op.sih_heatmap;

import ch.hsr.geohash.GeoHash;
import ch.hsr.geohash.WGS84Point;
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

    @Override
    protected void setup(Context context) throws IOException, InterruptedException {
        Configuration conf = context.getConfiguration();
        conf.addResource("op-core.xml");

        vehicles = loadVehicle(conf);
        System.out.println("高速公路热度分析, CQ4[" + vehicles.size() + "] ... ");
    }

    @Override
    protected void map(LongWritable key, Text value, Context context) throws IOException, InterruptedException {
        String str = value.toString();
        if (str != null && str.length() > 0) {
            // 分割单行数据
            String[] arr = value.toString().split(separator);
            if (arr == null || arr.length < 67) {
                return;
            }

            String vehicle = arr[1];
            if (vehicles.contains(vehicle)) {
                String location = arr[7];
                String enLng = arr[12];
                String enLat = arr[13];
                String gpsSpeed = arr[20];
                if (StringUtils.isEmpty(location) || StringUtils.isEmpty(gpsSpeed) ||
                        StringUtils.isEmpty(enLng) || StringUtils.isEmpty(enLat)) {
                    return;
                }

                int status = Integer.valueOf(location);
                double speed = Double.valueOf(gpsSpeed);
                if (status == 1 && speed > 75) {
                    double lng = Double.valueOf(enLng);
                    double lat = Double.valueOf(enLat);
                    GeoHash geoHash = GeoHash.withCharacterPrecision(lat, lng, 8);
                    String geoStr = geoHash.toBinaryString();

                    WGS84Point hashPoint = geoHash.getPoint();
                    lng = hashPoint.getLongitude();
                    lat = hashPoint.getLatitude();
/*
                    Map param = new HashMap();
                    param.put("lng", enLng);
                    param.put("lat", enLat);
                    param.put("radius", 100);
                    try {
                        String json = HttpUtil.getForString("http://10.129.50.41:7001/road/regeocode", param);
                        System.out.println(json);

                        Map dataMap = JacksonUtil.toObject(json, HashMap.class);
                        boolean expressway = (boolean) dataMap.get("expressway");
                        if (expressway) {
                            String geohash = (String) dataMap.get("geohash");
                            String name = (String) dataMap.get("name");

                            Map lnglat = (Map) dataMap.get("correctiveLocation");
                            double lng = (double) lnglat.get("lng");
                            double lat = (double) lnglat.get("lat");

                            WorkKey workKey = new WorkKey();
                            workKey.setGeohash(geohash);
                            workKey.setLng(lng);
                            workKey.setLat(lat);

                            WorkValue workValue = new WorkValue();
                            workValue.setName(StringUtils.isNotEmpty(name) ? name : vehicle);
                            context.write(workKey, workValue);

                            System.out.println("vehicle:" + vehicle + "[" + geohash + "," + name + "]");
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
*/
                    WorkKey workKey = new WorkKey();
                    workKey.setGeohash(geoStr);
                    workKey.setLng(lng);
                    workKey.setLat(lat);

                    WorkValue workValue = new WorkValue();
                    workValue.setName(geoStr);
                    context.write(workKey, workValue);
                }
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
}
