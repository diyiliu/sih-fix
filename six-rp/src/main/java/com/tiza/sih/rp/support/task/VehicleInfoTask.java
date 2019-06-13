package com.tiza.sih.rp.support.task;

import com.tiza.sih.rp.support.util.SpringUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;

import java.util.*;

/**
 * Description: VehicleInfoTask
 * Author: DIYILIU
 * Update: 2019-06-13 10:25
 */

@Slf4j
public class VehicleInfoTask {

    public static Map<String, String> vehicleMap = new HashMap();

    private JdbcTemplate jdbcTemplate;

    public void execute() {
        log.info("刷新车辆信息 ... ");

        String sql = "SELECT t.vhcle,t.vhvin " +
                "FROM t_vehicle t " +
                "WHERE t.vhvin IS NOT NULL AND t.device_no IS NOT NULL";

        Map temp = new HashMap();
        SqlRowSet rowSet = jdbcTemplate.queryForRowSet(sql);
        while (rowSet.next()){
            temp.put(rowSet.getString("vhvin"), rowSet.getString("vhcle"));
        }

        Set oldKeys = vehicleMap.keySet();
        Set tempKeys = temp.keySet();

        Collection subKeys = CollectionUtils.subtract(oldKeys, tempKeys);
        for (Iterator iterator = subKeys.iterator(); iterator.hasNext(); ) {
            String key = (String) iterator.next();
            vehicleMap.remove(key);
        }
        vehicleMap.putAll(temp);
    }

    public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }
}
