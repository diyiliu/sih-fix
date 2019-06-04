package com.tiza.tstar.op.sih_heatmap;

import org.apache.hadoop.io.WritableComparable;
import org.apache.hadoop.io.WritableComparator;

/**
 * Description: GroupingComparator
 * Author: DIYILIU
 * Update: 2017-09-27 17:29
 */
public class GroupingComparator extends WritableComparator {

    public GroupingComparator() {
        super(WorkKey.class, true);
    }

    @Override
    public int compare(WritableComparable a, WritableComparable b) {
        WorkKey key1 = (WorkKey)a;
        WorkKey key2 = (WorkKey)b;

        return key1.getGeohash().compareTo(key2.getGeohash());
    }
}
