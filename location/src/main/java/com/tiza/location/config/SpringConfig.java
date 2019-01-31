package com.tiza.location.config;

import com.tiza.location.util.HBaseUtil;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;

import javax.annotation.Resource;

/**
 * Description: SpringConfig
 * Author: DIYILIU
 * Update: 2019-01-30 10:56
 */

@Configuration
@PropertySource("classpath:config.properties")
public class SpringConfig {

    @Resource
    private Environment environment;

    @Bean
    public HBaseUtil hbaseUtil(){
        org.apache.hadoop.conf.Configuration config = HBaseConfiguration.create();
        config.set("hbase.zookeeper.quorum", environment.getProperty("zk.quorum"));
        config.set("hbase.zookeeper.property.clientPort", "2181");
        config.set("hbase.zookeeper.session.timeout", "180000");

        HBaseUtil hbaseUtil = new HBaseUtil();
        hbaseUtil.setConfig(config);
        hbaseUtil.setTableName(environment.getProperty("hbase.table"));

        return hbaseUtil;
    }
}
