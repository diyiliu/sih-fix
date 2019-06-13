package com.tiza.sih.rp.support.util;

import com.tiza.sih.rp.support.model.Header;
import com.tiza.sih.rp.support.model.IDataProcess;
import com.tiza.sih.rp.support.protocol.GbSixDataProcess;

/**
 * Description: DataProcessUtil
 * Author: DIYILIU
 * Update: 2019-06-06 16:48
 */
public class DataProcessUtil {
    private final static GbSixDataProcess dataProcess = new GbSixDataProcess();

    static {
        try {
            dataProcess.register("com.tiza.sih.rp.support.protocol");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static Header parseHeader(byte[] bytes){

        return dataProcess.parseHeader(bytes);
    }

    public static IDataProcess getProcess(int cmd){

        return dataProcess.getBox().get(cmd);
    }
}
