package com.tiza.sih.rp.support.protocol.cmd;

import com.tiza.sih.rp.support.model.GbSixHeader;
import com.tiza.sih.rp.support.model.Header;
import com.tiza.sih.rp.support.protocol.GbSixDataProcess;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;

/**
 * Description: CMD_01
 * Author: DIYILIU
 * Update: 2019-06-06 16:17
 */
public class CMD_01 extends GbSixDataProcess {

    public CMD_01() {
        cmdId = 0x01;
    }

    @Override
    public void parse(byte[] content, Header header) {
        GbSixHeader sixHeader = (GbSixHeader) header;
        ByteBuf buf = Unpooled.copiedBuffer(content);
        // 解析时间
        fetchDate(sixHeader, buf);

        // 登入流水号
        int serial = buf.readUnsignedShort();

        byte[] simArr = new byte[20];
        buf.readBytes(simArr);
        // SIM 卡 ICCID号
        String sim = new String(simArr);
    }
}
