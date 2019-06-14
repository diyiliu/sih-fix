package com.tiza.sih.rp.support.model;

import lombok.Data;

/**
 * Description: KafkaMsg
 * Author: DIYILIU
 * Update: 2019-06-14 11:46
 */

@Data
public class KafkaMsg {

    public KafkaMsg() {
    }

    public KafkaMsg(String key, Object value) {
        this.key = key;
        this.value = value;
    }

    private String key;

    private Object value;
}
