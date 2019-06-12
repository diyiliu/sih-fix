package com.tiza.sih.rp.support.model;

import lombok.Data;

/**
 * Description: DataBody
 * Author: DIYILIU
 * Update: 2019-06-11 16:22
 */

@Data
public class DataBody {
    private String terminal;

    private int cmd;

    private long time;

    private String data;
}
