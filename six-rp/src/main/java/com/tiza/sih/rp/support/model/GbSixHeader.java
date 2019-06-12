package com.tiza.sih.rp.support.model;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Date;

/**
 * Description: GbSixHeader
 * Author: DIYILIU
 * Update: 2019-06-06 16:13
 */

@Data
@EqualsAndHashCode(callSuper = false)
public class GbSixHeader extends Header{

    private int cmd;
    private int version;
    private int encrypt;

    private int length;
    private String vin;
    private byte[] content = new byte[0];
    private int check;

    private Date gpsTime;
}
