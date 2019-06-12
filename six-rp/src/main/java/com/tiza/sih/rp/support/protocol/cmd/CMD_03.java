package com.tiza.sih.rp.support.protocol.cmd;

import com.tiza.sih.rp.support.model.Header;
import com.tiza.sih.rp.support.protocol.GbSixDataProcess;
import com.tiza.sih.rp.support.util.DataProcessUtil;

/**
 * Description: CMD_03
 * Author: DIYILIU
 * Update: 2019-06-11 14:01
 */
public class CMD_03 extends GbSixDataProcess {

    public CMD_03() {
        cmdId = 0x03;
    }

    @Override
    public void parse(byte[] content, Header header) {
        DataProcessUtil.getProcess(0x02).parse(content, header);
    }
}
