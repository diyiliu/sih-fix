package com.tiza.mstar.jh.config;

import com.tiza.mstar.jh.util.HBaseUtil;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

/**
 * Description: JhConfig
 * Author: DIYILIU
 * Update: 2019-06-17 19:10
 */

@Configuration
@ComponentScan("com.tiza.mstar.jh")
@PropertySource("classpath:config.properties")
public class JhConfig {

    @Value("${hbase.quorum}")
    private String zkQuorum;

    @Value("${hbase.table}")
    private String hbaseTable;

    @Bean
    public HBaseUtil hbaseUtil(){
        org.apache.hadoop.conf.Configuration config = HBaseConfiguration.create();
        config.set("hbase.zookeeper.quorum", zkQuorum);
        config.set("hbase.zookeeper.property.clientPort", "2181");
        config.set("hbase.zookeeper.session.timeout", "180000");

        HBaseUtil hbaseUtil = new HBaseUtil();
        hbaseUtil.setConfig(config);
        hbaseUtil.setTableName(hbaseTable);

        return hbaseUtil;
    }

}
