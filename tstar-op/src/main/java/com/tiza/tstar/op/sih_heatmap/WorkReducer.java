package com.tiza.tstar.op.sih_heatmap;

import org.apache.hadoop.io.NullWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Reducer;

import java.io.IOException;
import java.util.Iterator;

/**
 * Description: WorkReducer
 * Author: DIYILIU
 * Update: 2019-06-04 09:58
 */
public class WorkReducer extends Reducer<WorkKey, WorkValue, Text, NullWritable> {

    @Override
    protected void setup(Context context) throws IOException, InterruptedException {

        System.out.println("WorkReducer" + 123);
    }

    @Override
    protected void reduce(WorkKey key, Iterable<WorkValue> values, Context context) throws IOException, InterruptedException {
        int count = 0;
        for (Iterator<WorkValue> iterator = values.iterator(); iterator.hasNext(); ) {
            WorkValue v = iterator.next();

            count++;
            System.out.println(v.getName());
        }

        System.out.println(key.getVehicle() + ":" + count);
    }
}
