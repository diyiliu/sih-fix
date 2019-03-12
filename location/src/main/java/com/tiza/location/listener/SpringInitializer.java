package com.tiza.location.listener;

import com.tiza.location.util.HBaseUtil;
import com.tiza.plugin.model.Point;
import com.tiza.plugin.model.Position;
import com.tiza.plugin.util.DateUtil;
import com.tiza.plugin.util.GpsCorrectUtil;
import com.tiza.plugin.util.HttpUtil;
import com.tiza.plugin.util.JacksonUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.*;

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

    @Value("${amap.url}")
    private String amapUrl;

    @Value("${amap.key}")
    private String amapKey;


    @Override
    public void onApplicationEvent(ApplicationEvent event) {
        // 时间范围 [2019-01-25, 2019-02-26)
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(0);
        calendar.set(Calendar.YEAR, 2019);
        calendar.set(Calendar.MONTH, 0);
        calendar.set(Calendar.DAY_OF_MONTH, 25);

        // 开始时间
        Date start = calendar.getTime();
        //System.out.println(DateUtil.dateToString(start));

        calendar.add(Calendar.DAY_OF_MONTH, 28);
        // 结束时间
        Date end = calendar.getTime();
        //System.out.println(DateUtil.dateToString(end));

        String sql =
                "SELECT  " +
                "  t.`id`, " +
                "  t.`vhcle`, " +
                "  t.`action_date`, " +
                "  t.`lon`, " +
                "  t.`lat` " +
                "FROM " +
                "  t_delivery_history t  " +
                "WHERE t.`action_date` BETWEEN ? AND ? " +
                "  AND t.`vehicle_location` IS NULL " +
                "ORDER BY t.`action_date`";

        List list = jdbcTemplate.queryForList(sql, new Object[]{start, end});
        log.info("=================== 运输扫描修复数据总数: [{}] ===================", list.size());

        int count = 0;
        for (int i = 0; i < list.size(); i++) {
            Map data = (Map) list.get(i);
            int id = (int) data.get("id");
            String vhcle = (String) data.get("vhcle");
            Date date = (Date) data.get("action_date");

            Double lng = null;
            Double lat = null;
            if (data.get("lon") != null && data.get("lat") != null) {
                lng = ((BigDecimal) data.get("lon")).doubleValue();
                lat = ((BigDecimal) data.get("lat")).doubleValue();
            }

            try {
                long eTime = date.getTime();
                long sTime = date.getTime() - 24 * 3600 * 1000;

                Position position = hbaseUtil.fetchPosition(vhcle, sTime, eTime);
                log.info("查询 [{}, {}] HBase 位置数据: {}", vhcle, DateUtil.dateToString(date), JacksonUtil.toJson(position));

                if (position != null) {
                    Double distance = null;
                    if (lng != null && lat != null) {
                        distance = GpsCorrectUtil.distanceP2P(new Point(lng, lat), new Point(position.getEnLng(), position.getEnLat()));
                    }

                    Map<String, String> posMap = converse(position.getEnLng(), position.getEnLat());
                    position.setAddress(posMap.get("address"));
                    position.setProvince(posMap.get("province"));
                    position.setCity(posMap.get("city"));
                    position.setArea(posMap.get("area"));

                    // 更新数据库
                    updateDb(id, distance, position);
                    count++;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        log.info("=================== 运输扫描修复位置数据完成, 数量[{}] ===================", count);
    }




    /**
     * 更新数据库
     * @param id
     * @param distance
     * @param position
     */
    public void updateDb(int id, Double distance, Position position) {
        String sql = "  UPDATE  " +
                "    t_delivery_history t  " +
                "  SET " +
                "    t.`vehicle_lon` = ?, " +
                "    t.`vehicle_lat` = ?, " +
                "    t.`vehicle_location` = ?, " +
                "    t.`vehicle_province` = ?, " +
                "    t.`vehicle_city` = ?, " +
                "    t.`vehicle_district` = ?, " +
                "    t.`distance` = ?  " +
                "  WHERE t.`id` = ?";

        Object[] param = new Object[]{position.getEnLng(), position.getEnLat(), position.getAddress(),
                position.getProvince(), position.getCity(), position.getArea(), distance, id};
        log.info("执行 SQL: {}, 参数: {}", sql, param);

        jdbcTemplate.update(sql, param);
    }

    /**
     * 高德逆地址解析
     *
     * @param lng
     * @param lat
     * @return
     * @throws Exception
     */
    public Map<String, String> converse(double lng, double lat) throws Exception {
        Map param = new HashMap();
        param.put("output", "json");
        param.put("key", amapKey);
        param.put("location", lng + "," + lat);

        Map m = null;
        String result = HttpUtil.getForString(amapUrl + "geocode/regeo", param);
        Map map = JacksonUtil.toObject(result, HashMap.class);
        int status = Integer.parseInt(String.valueOf(map.get("status")));
        if (status == 1) {
            Map regeo = (Map) map.get("regeocode");
            Map compoment = (Map) regeo.get("addressComponent");

            String address = (String) regeo.get("formatted_address");
            String province = (String) compoment.get("province");

            String city = "";
            Object cityObj = compoment.get("city");
            if (cityObj instanceof String) {
                city = cityObj.toString();
            } else if (cityObj instanceof List) {
                List<String> cityList = (List<String>) cityObj;
                if (CollectionUtils.isNotEmpty(cityList)) {
                    city = cityList.get(0);
                }
            }
            String area = (String) compoment.get("district");

            m = new HashMap();
            m.put("address", address);
            m.put("province", province);
            m.put("city", StringUtils.isEmpty(city) ? province : city);
            m.put("area", area);
        }

        return m;
    }
}
