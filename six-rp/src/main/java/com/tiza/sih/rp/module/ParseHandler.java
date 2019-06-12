package com.tiza.sih.rp.module;

import backtype.storm.task.OutputCollector;
import backtype.storm.task.TopologyContext;
import backtype.storm.topology.OutputFieldsDeclarer;
import backtype.storm.topology.base.BaseRichBolt;
import backtype.storm.tuple.Tuple;
import com.tiza.sih.rp.support.model.DataBody;
import com.tiza.sih.rp.support.model.GbSixHeader;
import com.tiza.sih.rp.support.model.IDataProcess;
import com.tiza.sih.rp.support.util.CommonUtil;
import com.tiza.sih.rp.support.util.DataProcessUtil;
import com.tiza.sih.rp.support.util.JacksonUtil;

import java.io.IOException;
import java.util.Map;

/**
 * Description: ParseHandler
 * Author: DIYILIU
 * Update: 2019-06-06 15:02
 */
public class ParseHandler extends BaseRichBolt {

    private OutputCollector collector;

    @Override
    public void prepare(Map stormConf, TopologyContext context, OutputCollector collector) {
        this.collector = collector;
    }

    @Override
    public void execute(Tuple tuple) {
        collector.ack(tuple);

        String kafkaMsg = tuple.getString(0);
        try {
            DataBody body = JacksonUtil.toObject(kafkaMsg, DataBody.class);
            String data = body.getData();
            byte[] bytes = CommonUtil.hexStringToBytes(data);

            GbSixHeader header = (GbSixHeader) DataProcessUtil.parseHeader(bytes);
            int cmd = header.getCmd();
            IDataProcess process = DataProcessUtil.getProcess(cmd);
            if (process != null) {
                process.parse(header.getContent(), header);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void declareOutputFields(OutputFieldsDeclarer declarer) {

    }
}
