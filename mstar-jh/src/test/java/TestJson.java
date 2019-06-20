import com.tiza.mstar.jh.model.CanBody;
import com.tiza.plugin.util.JacksonUtil;
import org.apache.commons.beanutils.BeanUtils;
import org.junit.Test;

/**
 * Description: TestJson
 * Author: DIYILIU
 * Update: 2019-06-18 10:31
 */
public class TestJson {
    
    @Test
    public void test() throws Exception{
        String str = "{\"id\":123," +
                "\"BIT19\": \"0.0\"," +
                "\"INT5\": \"11.0\"," +
                "\"BIT14\": \"1.0\"," +
                "\"DECIMAL8\": \"78.0\"," +
                "\"BIT8\": \"1.0\"," +
                "\"INT4\": \"5.0\"," +
                "\"BIT1\": \"1.0\"," +
                "\"DECIMAL2\": \"12.34\"," +
                "\"BIT11\": \"1.0\"," +
                "\"BIT10\": \"1.0\"," +
                "\"DECIMAL41\": \"25789.0\"," +
                "\"BIT16\": \"0.0\"," +
                "\"BIT7\": \"1.0\"," +
                "\"BIT2\": \"1.0\"," +
                "\"INT3\": \"4.0\"," +
                "\"INT10\": \"0.0\"," +
                "\"INT6\": \"2.0\"," +
                "\"BIT20\": \"0.0\"," +
                "\"DECIMAL44\": \"7632.0\"," +
                "\"BIT4\": \"1.0\"," +
                "\"BIT12\": \"1.0\"," +
                "\"INT1\": \"3.0\"," +
                "\"BYTE1\": \"78.0\"," +
                "\"INT9\": \"112.0\"," +
                "\"BYTE4\": \"1.0\"," +
                "\"DECIMAL42\": \"45782.0\"," +
                "\"BIT5\": \"1.0\"," +
                "\"BIT22\": \"1.0\"," +
                "\"BIT21\": \"1.0\"," +
                "\"DECIMAL43\": \"452.6\"," +
                "\"DECIMAL3\": \"10.600000000000001\"," +
                "\"BYTE3\": \"42.0\"," +
                "\"INT7\": \"1800.0\"," +
                "\"DECIMAL4\": \"167.5\"," +
                "\"BIT17\": \"0.0\"," +
                "\"BIT6\": \"1.0\"," +
                "\"STRING1\": \"236.0\"," +
                "\"BIT3\": \"1.0\"," +
                "\"BIT13\": \"1.0\"," +
                "\"DECIMAL1\": \"56.0\"," +
                "\"BIT18\": \"0.0\"," +
                "\"INT2\": \"2.0\"," +
                "\"BIT9\": \"1.0\"," +
                "\"INT8\": \"2145.0\"," +
                "\"BYTE2\": \"28.0\"" +
                "}";

        CanBody can = JacksonUtil.toObject(str, CanBody.class);

        System.out.println(BeanUtils.getSimpleProperty(can, "mileage"));
    }
}
