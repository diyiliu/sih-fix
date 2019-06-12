package com.tiza.sih.rp.support.protocol.cmd;

import com.tiza.sih.rp.support.model.GbSixHeader;
import com.tiza.sih.rp.support.model.Header;
import com.tiza.sih.rp.support.protocol.GbSixDataProcess;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;

/**
 * Description: CMD_04
 * Author: DIYILIU
 * Update: 2019-06-11 14:05
 */
public class CMD_04 extends GbSixDataProcess {

    public CMD_04() {
        cmdId = 0x04;
    }

    @Override
    public void parse(byte[] content, Header header) {
        GbSixHeader sixHeader = (GbSixHeader) header;
        ByteBuf buf = Unpooled.copiedBuffer(content);
        // 解析时间
        fetchDate(sixHeader, buf);

        // 登入流水号
        int serial = buf.readUnsignedShort();
    }
}
