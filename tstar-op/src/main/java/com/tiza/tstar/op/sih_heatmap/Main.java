package com.tiza.tstar.op.sih_heatmap;

import cn.com.tiza.tstar.op.client.BaseJob;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.input.TextInputFormat;
import org.apache.hadoop.mapreduce.lib.output.NullOutputFormat;

/**
 * Description: Main
 * Author: DIYILIU
 * Update: 2019-06-04 09:51
 */
public class Main extends BaseJob {

    @Override
    public Job getJob() throws Exception {
        job.setJarByClass(Main.class);

        job.setGroupingComparatorClass(GroupingComparator.class);
        job.setMapperClass(WorkMapper.class);
        job.setMapOutputKeyClass(WorkKey.class);
        job.setMapOutputValueClass(WorkValue.class);
        job.setInputFormatClass(TextInputFormat.class);

        job.setReducerClass(WorkReducer.class);
        job.setOutputFormatClass(NullOutputFormat.class);
        job.setNumReduceTasks(1);

        return job;
    }

    public static void main(String[] args) throws Exception{
        Job job = new Main().getJob();
        FileInputFormat.addInputPath(job, new Path(args[0]));
        System.exit(job.waitForCompletion(true) ? 0 : 1);
    }
}
