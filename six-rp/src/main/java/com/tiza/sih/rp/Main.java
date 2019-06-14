package com.tiza.sih.rp;

import backtype.storm.Config;
import backtype.storm.LocalCluster;
import backtype.storm.StormSubmitter;
import backtype.storm.spout.SchemeAsMultiScheme;
import backtype.storm.topology.TopologyBuilder;
import com.tiza.sih.rp.module.ParseHandler;
import com.tiza.sih.rp.support.util.JacksonUtil;
import org.apache.commons.cli.*;
import storm.kafka.KafkaSpout;
import storm.kafka.SpoutConfig;
import storm.kafka.StringScheme;
import storm.kafka.ZkHosts;

import java.io.InputStream;
import java.util.Properties;

/**
 * Description: Main
 * Author: DIYILIU
 * Update: 2019-06-06 14:50
 */
public class Main {

    public static void main(String[] args) throws Exception{
        System.out.println(JacksonUtil.toJson(args));
        Options options = new Options();
        Option subOp = new Option("l", "local", true, "local or cluster[0: cluster,1: local]");
        subOp.setRequired(true);
        options.addOption(subOp);

        CommandLineParser parser = new PosixParser();
        CommandLine cli = parser.parse(options, args);

        Properties properties = new Properties();
        try (InputStream in = ClassLoader.getSystemResourceAsStream("sih.properties")) {
            properties.load(in);
            String topic = properties.getProperty("kafka.raw-topic");
            ZkHosts zkHosts = new ZkHosts(properties.getProperty("kafka.zk-host"));

            SpoutConfig spoutConfig = new SpoutConfig(zkHosts, topic, "", "sih_gb6");
            spoutConfig.scheme = new SchemeAsMultiScheme(new StringScheme());

            TopologyBuilder builder = new TopologyBuilder();
            builder.setSpout("spout", new KafkaSpout(spoutConfig), 1);
            builder.setBolt("bolt1", new ParseHandler(), 1).shuffleGrouping("spout");
            // builder.setBolt("bolt2", new ReportTripBolt(), 1).fieldsGrouping("bolt1", new Fields("vehicleId"));

            Config conf = new Config();
            conf.setDebug(false);
            // 限流
            conf.put(backtype.storm.Config.TOPOLOGY_MAX_SPOUT_PENDING, 64);

            // 本地模式 + 集群模式
            if (cli.getOptionValue("local").equals("1")) {
                LocalCluster localCluster = new LocalCluster();
                localCluster.submitTopology("sih_gb6", conf, builder.createTopology());
            } else {
                StormSubmitter.submitTopology("sih_gb6", conf, builder.createTopology());
            }
        }
    }
}
