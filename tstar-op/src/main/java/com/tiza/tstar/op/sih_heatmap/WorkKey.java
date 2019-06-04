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

    private String geohash;

    private Double lng;

    private Double lat;

    public String getGeohash() {
        return geohash;
    }

    public void setGeohash(String geohash) {
        this.geohash = geohash;
    }

    public Double getLng() {
        return lng;
    }

    public void setLng(Double lng) {
        this.lng = lng;
    }

    public Double getLat() {
        return lat;
    }

    public void setLat(Double lat) {
        this.lat = lat;
    }

    @Override
    public int compareTo(WorkKey o) {
        return geohash.compareTo(o.getGeohash());
    }


    @Override
    public void write(DataOutput dataOutput) throws IOException {

        Text.writeString(dataOutput, geohash);
        dataOutput.writeDouble(lng);
        dataOutput.writeDouble(lat);
    }

    @Override
    public void readFields(DataInput dataInput) throws IOException {

        this.geohash = Text.readString(dataInput);
        this.lng = dataInput.readDouble();
        this.lat = dataInput.readDouble();
    }
}
