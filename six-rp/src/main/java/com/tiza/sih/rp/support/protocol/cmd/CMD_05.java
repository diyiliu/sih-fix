package com.tiza.sih.rp.support.protocol.cmd;

import com.tiza.sih.rp.support.model.Header;
import com.tiza.sih.rp.support.protocol.GbSixDataProcess;

/**
 * Description: CMD_05
 * Author: DIYILIU
 * Update: 2019-06-11 14:06
 */
public class CMD_05 extends GbSixDataProcess {

    public CMD_05() {
        cmdId = 0x05;
    }

    @Override
    public void parse(byte[] content, Header header) {


    }
}
