package com.tiza.sih.rp.support.protocol;

import com.tiza.sih.rp.support.model.GbSixHeader;
import com.tiza.sih.rp.support.model.Header;
import com.tiza.sih.rp.support.model.IDataProcess;
import com.tiza.sih.rp.support.util.CommonUtil;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import org.reflections.Reflections;

import java.util.*;

/**
 * Description: GbSixDataProcess
 * Author: DIYILIU
 * Update: 2019-06-06 16:16
 */
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

    public Map<Integer, GbSixDataProcess> getBox() {
        return processBox;
    }
}
