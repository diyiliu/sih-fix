import com.tiza.sih.rp.support.model.KafkaMsg;
import com.tiza.sih.rp.support.util.KafkaUtil;
import org.junit.Test;

/**
 * Description: TestKafka
 * Author: DIYILIU
 * Update: 2019-06-14 15:55
 */
public class TestKafka {

    @Test
    public void test() throws Exception{
        KafkaUtil kafkaUtil = new KafkaUtil();
        kafkaUtil.setBrokers("xg153:9092,xg154:9092,xg155:9092");
        kafkaUtil.setTopic("sih_gb6_workdata");
        kafkaUtil.setSerializer("kafka.serializer.DefaultEncoder");
        kafkaUtil.init();

        while (true){
            KafkaMsg msg = new KafkaMsg();
            msg.setKey("1");
            msg.setValue("7F7F1560498873373".getBytes());
            KafkaUtil.send(msg);

            Thread.sleep(3000);
        }
    }
}
