package com.tiza.tstar.op.sih_heatmap;

import org.apache.hadoop.io.Text;
import org.apache.hadoop.io.WritableComparable;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

/**
 * Description: WorkValue
 * Author: DIYILIU
 * Update: 2019-06-04 10:00
 */
public class WorkValue implements WritableComparable<WorkValue> {

    private String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public int compareTo(WorkValue o) {
        return name.compareTo(o.getName());
    }

    @Override
    public void write(DataOutput dataOutput) throws IOException {

        Text.writeString(dataOutput, name);
    }

    @Override
    public void readFields(DataInput dataInput) throws IOException {

        this.name = Text.readString(dataInput);
    }
}
