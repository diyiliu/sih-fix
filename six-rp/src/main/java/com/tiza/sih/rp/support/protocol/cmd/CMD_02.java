package com.tiza.sih.rp.support.protocol.cmd;

import com.tiza.sih.rp.support.model.GbSixHeader;
import com.tiza.sih.rp.support.model.Header;
import com.tiza.sih.rp.support.protocol.GbSixDataProcess;
import com.tiza.sih.rp.support.util.CommonUtil;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;

import java.util.ArrayList;
import java.util.List;

/**
 * Description: CMD_02
 * Author: DIYILIU
 * Update: 2019-06-11 10:05
 */
public class CMD_02 extends GbSixDataProcess {

    public CMD_02() {
        cmdId = 0x02;
    }

    @Override
    public void parse(byte[] content, Header header) {
        GbSixHeader sixHeader = (GbSixHeader) header;
        ByteBuf buf = Unpooled.copiedBuffer(content);
        // 解析时间
        fetchDate(sixHeader, buf);

        List list = new ArrayList();
        while (buf.readableBytes() > 1) {
            int type = buf.readUnsignedByte();

            // OBD 信息
            if (0x01 == type) {

                parseOBD(buf, list);
            }
            // 数据流信息
            else if (0x02 == type) {

                parseFlow(buf, list);
            }
        }
    }


    private void parseOBD(ByteBuf buf, List paramValues) {
        int protocol = buf.readUnsignedByte();
        int mil = buf.readUnsignedByte();

        byte[] supStatus = new byte[2];
        buf.readBytes(supStatus);

        byte[] readyStatus = new byte[2];
        buf.readBytes(readyStatus);

        byte[] vinBytes = new byte[17];
        buf.readBytes(vinBytes);
        String vin = new String(vinBytes);

        byte[] softBytes = new byte[18];
        buf.readBytes(softBytes);
        String soft = new String(softBytes);

        byte[] cvnBytes = new byte[18];
        buf.readBytes(cvnBytes);
        String cvn = new String(cvnBytes);

        // 待定
        byte[] iuprBytes = new byte[36];
        buf.readBytes(iuprBytes);

        // 故障信息
        int faultNum = buf.readUnsignedByte();
        if (faultNum > 0 && faultNum != 0xFE) {
            for (int i = 0; i < faultNum; i++) {
                int code = buf.readInt();
            }
        }
    }

    private void parseFlow(ByteBuf buf, List paramValues) {
        int speed = buf.readUnsignedShort();
        if (0xFFFF != speed) {
            CommonUtil.keepDecimal(speed, 1 / 256, 3);
        }

        int pressure = buf.readUnsignedByte();
        if (0xFFFF != pressure) {
            CommonUtil.keepDecimal(pressure, 0.5, 1);
        }

        // 扭矩
        int torque = buf.readUnsignedByte();
        if (0xFF != torque) {
            torque = torque - 125;
        }

        // 摩擦扭矩
        int frictionTq = buf.readUnsignedByte();
        if (0xFF != frictionTq) {
            frictionTq = frictionTq - 125;
        }

        // 发动机转速
        int rpm = buf.readUnsignedShort();
        if (0xFFFF != rpm) {
            CommonUtil.keepDecimal(rpm, 0.125, 3);
        }

        // 燃料流量
        int ff = buf.readUnsignedShort();
        if (0xFFFF != ff) {
            CommonUtil.keepDecimal(ff, 0.05, 2);
        }

        int upNox = buf.readUnsignedShort();
        if (0xFFFF != ff) {
            CommonUtil.keepDecimal(upNox, 0.05, 2);
        }

        int downNox = buf.readUnsignedShort();
        if (0xFFFF != ff) {
            CommonUtil.keepDecimal(downNox, 0.05, 2);
        }

        // 反应剂余量
        int reactant = buf.readUnsignedByte();
        if (0xFF != reactant) {
            CommonUtil.keepDecimal(reactant, 0.4, 1);
        }

        // 进气量
        int inflow = buf.readUnsignedShort();
        if (0xFFFF != inflow) {
            CommonUtil.keepDecimal(inflow, 0.05, 2);
        }

        int inTemp = buf.readUnsignedShort();
        if (0xFFFF != inTemp) {
            double d = CommonUtil.keepDecimal(inTemp, 0.03125, 5) - 273;
        }

        int outTemp = buf.readUnsignedShort();
        if (0xFFFF != outTemp) {
            double d = CommonUtil.keepDecimal(outTemp, 0.03125, 5) - 273;
        }

        // DFP 压差
        int dfpDif = buf.readUnsignedShort();
        if (0xFFFF != dfpDif) {
            CommonUtil.keepDecimal(dfpDif, 0.1, 1);
        }

        // 冷却液温度
        int coolantTemp = buf.readUnsignedByte();
        if (0xFF != coolantTemp) {
            int i = coolantTemp - 40;
        }

        // 油位
        int fuelLevel = buf.readUnsignedByte();
        if (0xFF != fuelLevel) {
            CommonUtil.keepDecimal(fuelLevel, 0.4, 1);
        }

        // 定位状态
        int location = buf.readByte();

        //0:有效;1:无效
        int status = location & 0x01;
        int latDir = location & 0x02;
        int lngDir = location & 0x04;

        long lng = buf.readUnsignedInt();
        long lat = buf.readUnsignedInt();

        CommonUtil.keepDecimal(lng * (lngDir == 0 ? 1 : -1), 0.000001, 6);
        CommonUtil.keepDecimal(lat * (latDir == 0 ? 1 : -1), 0.000001, 6);

        long mileage = buf.readUnsignedInt();
        if (0xFFFFFFFF != mileage) {
            CommonUtil.keepDecimal(mileage, 0.1, 1);
        }

    }
}
