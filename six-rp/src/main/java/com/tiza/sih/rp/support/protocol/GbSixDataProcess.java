package com.tiza.sih.rp.support.protocol;

import com.google.common.collect.Lists;
import com.tiza.sih.rp.support.model.*;
import com.tiza.sih.rp.support.util.CommonUtil;
import com.tiza.sih.rp.support.util.JacksonUtil;
import com.tiza.sih.rp.support.util.KafkaUtil;
import com.tiza.sih.rp.support.util.SpringUtil;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import lombok.extern.slf4j.Slf4j;
import org.reflections.Reflections;
import org.springframework.jdbc.core.JdbcTemplate;

import java.nio.charset.Charset;
import java.util.*;

/**
 * Description: GbSixDataProcess
 * Author: DIYILIU
 * Update: 2019-06-06 16:16
 */

@Slf4j
public class GbSixDataProcess implements IDataProcess {
    protected int cmdId = 0xFF;

    private static Map<Integer, GbSixDataProcess> processBox = new HashMap();

    @Override
    public Header parseHeader(byte[] bytes) {
        ByteBuf buf = Unpooled.copiedBuffer(bytes);
        // 读取头标志[0x23,0x23]
        buf.readBytes(new byte[2]);

        // 命令标识
        int cmd = buf.readUnsignedByte();

        byte[] vinBytes = new byte[17];
        buf.readBytes(vinBytes);
        String vin = new String(vinBytes);

        // 软件版本号
        int version = buf.readUnsignedByte();
        // 加密方式
        int encrypt = buf.readUnsignedByte();

        int length = buf.readUnsignedShort();
        byte[] content = new byte[length];
        buf.readBytes(content);

        GbSixHeader header = new GbSixHeader();
        header.setCmd(cmd);
        header.setVin(vin);
        header.setVersion(version);
        header.setEncrypt(encrypt);
        header.setContent(content);

        return header;
    }

    @Override
    public void parse(byte[] content, Header header) {

    }

    @Override
    public byte[] pack(Header header, Object... argus) {
        return new byte[0];
    }

    @Override
    public void init() {
        processBox.put(cmdId, this);
    }

    public void detach(GbSixHeader header, List<PackUnit> paramValues) {
        int cmd = header.getCmd();
        String vehicle = header.getVehicle();

        // 更新当前位置信息
        if (0x02 == cmd) {
            List list = Lists.newArrayList(vehicle, header.getGpsTime());
            StringBuffer pStr = new StringBuffer();
            StringBuffer vStr = new StringBuffer();

            List keys = new ArrayList();
            for (int i = 0; i < paramValues.size(); i++) {
                PackUnit unit = paramValues.get(i);
                int id = unit.getId();
                if (keys.contains(id)) {
                    continue;
                }
                keys.add(id);
                Map<String, Object> map = unit.getData();
                for (Iterator<String> iterator = map.keySet().iterator(); iterator.hasNext(); ) {
                    String key = iterator.next();
                    Object value = map.get(key);

                    pStr.append(",").append(CommonUtil.underline(key));
                    vStr.append(",?");
                    list.add(formatValue(value));
                }
            }

            String sql = "REPLACE INTO t_vehicle_info_gbsix(vhcle,update_time" + pStr + ") VALUES(?,?" + vStr + ")";
            log.info("更新车辆[{}]实时信息 ... ", vehicle);
            JdbcTemplate jdbcTemplate = SpringUtil.getBean("jdbcTemplate");
            jdbcTemplate.update(sql, list.toArray());
        }

        // 写入 TStar kafka
        String jsonBody = JacksonUtil.toJson(paramValues);

        byte[] terminalArr = vehicle.getBytes();
        int tLen = terminalArr.length;

        byte[] msgArr = jsonBody.getBytes(Charset.forName("UTF-8"));
        int mLen = msgArr.length;

        ByteBuf buf = Unpooled.buffer(21 + tLen + mLen);
        buf.writeByte(tLen);
        buf.writeBytes(terminalArr);
        buf.writeLong(header.getGpsTime().getTime());
        buf.writeInt(cmd);
        buf.writeInt(0);
        buf.writeInt(mLen);
        buf.writeBytes(msgArr);
        KafkaUtil.send(new KafkaMsg(vehicle, buf.array()));
        log.info("车辆[{}]写入Kafka[{}] ... ", vehicle, jsonBody);
    }

    /**
     * 解析协议中时间
     *
     * @param header
     * @param buf
     */
    public void fetchDate(GbSixHeader header, ByteBuf buf) {
        // 数据采集时间
        byte[] dateArr = new byte[6];
        buf.readBytes(dateArr);

        Date date = CommonUtil.bytesToDate(dateArr);
        header.setGpsTime(date);
    }

    public void register(String path) throws Exception {
        Reflections reflections = new Reflections(path);
        Set set = reflections.getSubTypesOf(this.getClass());

        for (Iterator<Class<?>> iterator = set.iterator(); iterator.hasNext(); ) {
            Class clz = iterator.next();
            IDataProcess process = (IDataProcess) clz.newInstance();
            process.init();
        }
    }

    public Object formatValue(Object obj) {
        if (obj instanceof Map || obj instanceof Collection) {

            return JacksonUtil.toJson(obj);
        }

        return obj;
    }

    public Map<Integer, GbSixDataProcess> getBox() {
        return processBox;
    }
}
