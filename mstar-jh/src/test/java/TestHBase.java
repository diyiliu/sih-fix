import com.tiza.mstar.jh.util.HBaseUtil;
import com.tiza.plugin.util.JacksonUtil;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.joda.time.DateTime;
import org.junit.Test;

import java.util.List;

/**
 * Description: TestHBase
 * Author: DIYILIU
 * Update: 2019-06-18 09:11
 */
public class TestHBase {


    @Test
    public void test() throws Exception{
        org.apache.hadoop.conf.Configuration config = HBaseConfiguration.create();
        config.set("hbase.zookeeper.quorum", "xg153");
        config.set("hbase.zookeeper.property.clientPort", "2181");
        config.set("hbase.zookeeper.session.timeout", "180000");

        HBaseUtil hbaseUtil = new HBaseUtil();
        hbaseUtil.setConfig(config);
        hbaseUtil.setTableName("tstar:MStarTizaTrackdata");

        DateTime dateTime = new DateTime();
        List list =   hbaseUtil.fetchData("331", dateTime.minusMonths(2).getMillis(), dateTime.getMillis());
        String str = JacksonUtil.toJson(list);
        System.out.println(str);
    }
}
