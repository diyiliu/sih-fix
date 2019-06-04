package com.tiza.tstar.op.sih_heatmap;

import org.apache.hadoop.io.Text;
import org.apache.hadoop.io.WritableComparable;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

/**
 * Description: WorkKey
 * Author: DIYILIU
 * Update: 2019-06-04 09:59
 */
public class WorkKey implements WritableComparable<WorkKey> {

    private String vehicle;

    public String getVehicle() {
        return vehicle;
    }

    public void setVehicle(String vehicle) {
        this.vehicle = vehicle;
    }

    @Override
    public int compareTo(WorkKey o) {
        return vehicle.compareTo(o.getVehicle());
    }


    @Override
    public void write(DataOutput dataOutput) throws IOException {

        Text.writeString(dataOutput, vehicle);
    }

    @Override
    public void readFields(DataInput dataInput) throws IOException {

        this.vehicle = Text.readString(dataInput);
    }
}
