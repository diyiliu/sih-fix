import ch.hsr.geohash.GeoHash;
import com.google.common.collect.Lists;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.HTableDescriptor;
import org.apache.hadoop.hbase.client.*;
import org.apache.hadoop.hbase.util.Bytes;
import org.joda.time.DateTime;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Description: TestHBase
 * Author: DIYILIU
 * Update: 2019-06-04 18:40
 */
public class TestHBase {

    @Test
    public void test2(){
    }

    @Test
    public void test(){
        getAllTables();
    }

    @Test
    public void test1() throws Exception{
        Date sDate = DateUtil.stringToDate("2019-05-30", "yyyy-MM-dd");
        Date eDate = DateUtil.stringToDate("2019-05-31", "yyyy-MM-dd");

        List<Integer> list = Lists.newArrayList();
        DateTime s = new DateTime(sDate);
        do {
            String str = DateUtil.dateToString(sDate, "%1$tY%1$tm%1$td");
            int day = Integer.valueOf(str);
            s = s.plusDays(1);
            sDate = s.toDate();
            list.add(day);
        } while (!eDate.before(sDate));

        byte[] family = Bytes.toBytes("1");
        byte[] start = Bytes.toBytes("00");

        byte[] lngColumn = Bytes.toBytes("lng");
        byte[] latColumn = Bytes.toBytes("lat");

        Scan scan = new Scan();
        scan.addColumn(family, lngColumn);
        scan.addColumn(family, latColumn);
        for (Integer i: list){
            scan.addColumn(family, Bytes.toBytes(i));
        }


//        Filter filter = new RowFilter(CompareFilter.CompareOp.LESS, new BinaryComparator(start));
//        scan.setFilter(filter);

        org.apache.hadoop.conf.Configuration hconf = HBaseConfiguration.create();
        //配置参数，第一个是主机名，第二个是端口号
        hconf.set("hbase.zookeeper.quorum", "10.129.96.13");
        HConnection conn = HConnectionManager.createConnection(hconf);
        HTable table = (HTable) conn.getTable("tstar:SIH_HIGHWAY_HEATMAP");
        ResultScanner rs = table.getScanner(scan);

        System.out.println("OK");
        for (Result r : rs) {
            double lng = Bytes.toDouble(r.getValue(family, lngColumn));
            double lat = Bytes.toDouble(r.getValue(family, latColumn));

            int count = 0;
            for (Integer i: list){

                count += Bytes.toInt(r.getValue(family, Bytes.toBytes(i)));
            }

            System.out.println(lng + "," + lat + "," + count);
        }

    }

    public List getAllTables() {
        Configuration conf = HBaseConfiguration.create();
        //配置参数，第一个是主机名，第二个是端口号
        conf.set("hbase.zookeeper.quorum", "10.129.96.13");
        List<String> tables = new ArrayList();
        try {
            HBaseAdmin admin = new HBaseAdmin(conf);
            if (admin != null) {
                HTableDescriptor[] allTables = admin.listTables();
                for (HTableDescriptor tableDesc : allTables) {
                    tables.add(tableDesc.getNameAsString());
                    System.out.println(tableDesc.getNameAsString());
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return tables;
    }
}
