import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.tiza.sih.rp.support.model.GbSixHeader;
import com.tiza.sih.rp.support.model.IDataProcess;
import com.tiza.sih.rp.support.util.CommonUtil;
import com.tiza.sih.rp.support.util.DataProcessUtil;
import com.tiza.sih.rp.support.util.SpringUtil;
import org.junit.Test;

import javax.annotation.Resource;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Description: TestMain
 * Author: DIYILIU
 * Update: 2019-06-13 14:47
 */
public class TestMain {

    @Test
    public void test() {
        String str = "2323024C57485445535431323334353637383935110101131306030F193B000401010000EB00804C574854455354313233343536373839353630313630332D4532303030303030303030545A5748303030305830303030303030303031303230313032300000000000000000000000000000000000000000000000000000000000203131303030334142423033334646414143443033383943434544313239454400203232303042433033414242303333464641414344453830383943434544314544022621CA98B41B5601631CA8109AAA00172BAA2A2204A25B54000686C1D7020D043F00002AFD2031313030303341424230333346464141434430333839434345443132394544002032323030424330334142423033334646414143444538303839434345443145445F";
        byte[] bytes = CommonUtil.hexStringToBytes(str);
        SpringUtil.init();

        GbSixHeader header = (GbSixHeader) DataProcessUtil.parseHeader(bytes);
        header.setVehicle("007011");
        header.setGpsTime(new Date());

        int cmd = header.getCmd();
        IDataProcess process = DataProcessUtil.getProcess(cmd);
        if (process != null) {
            process.parse(header.getContent(), header);
        }
    }

    @Test
    public void test1() throws Exception {
        Map user = new HashMap();
        user.put("nameInfo", "abc");


        ObjectMapper mapper = new ObjectMapper();
        mapper.setPropertyNamingStrategy(PropertyNamingStrategy.SNAKE_CASE);
        String json = mapper.writeValueAsString(user);
        System.out.println(json);
    }

    @Test
    public void test2(){

        String abc = "NameInfoKeyHello";

        System.out.println(CommonUtil.underline(abc));

        System.out.println(CommonUtil.camel(abc));
    }
}
