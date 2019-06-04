import com.tiza.plugin.util.HttpUtil;
import com.tiza.plugin.util.JacksonUtil;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.junit.Test;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Description: TestGaoDe
 * Author: DIYILIU
 * Update: 2019-01-30 13:46
 */
public class TestGaoDe {

    @Test
    public void test1() {
        String str = "0200\t0070441427\t234213\t64606778396\t064606778396\t2019-01-25 12:45:12\t2019-01-25 12:45:16\t1\t1\t1\t106.523396\t29.682279\t106.527185\t29.679532\t重庆市\t重庆城区\t渝北区\t500000\t500100\t500112\t0\t0.0\t7.90\t\t0.000\t未定义\t0\t0\t53\t\t120\t7.0\t102.0\t";
        String[] strArr = str.split("\t");
        System.out.println(strArr[5]);
        System.out.println(strArr[12] + "," + strArr[13]);
    }

    @Test
    public void test2() throws Exception {
        String key = "dba607b5151015663f8e6bce14e94fa8";
        String url = "http://restapi.amap.com/v3/geocode/regeo";

        double lng = 117.3263454490;
        double lat = 34.1905057809;

//        double lng = 106.529108;
//        double lat = 29.683097;

        Map param = new HashMap();
        param.put("output", "json");
        param.put("key", key);
        param.put("location", lng + "," + lat);

        String result = HttpUtil.getForString(url, param);
        System.out.println(result);

        Map map = JacksonUtil.toObject(result, HashMap.class);
        int status = Integer.parseInt(String.valueOf(map.get("status")));
        if (status == 1) {
            Map regeo = (Map) map.get("regeocode");
            Map compoment = (Map) regeo.get("addressComponent");

            String address = (String) regeo.get("formatted_address");
            String province = (String) compoment.get("province");

            String city = "";
            Object cityObj = compoment.get("city");
            if (cityObj instanceof String) {
                city = cityObj.toString();
            } else if (cityObj instanceof List) {
                List<String> cityList = (List<String>) cityObj;
                if (CollectionUtils.isNotEmpty(cityList)) {
                    city = cityList.get(0);
                }
            }


            String area = (String) compoment.get("district");

            Map m = new HashMap();
            m.put("address", address);
            m.put("province", province);
            m.put("city", StringUtils.isEmpty(city) ? province : city);
            m.put("area", area);

            System.out.println(m);
        }
    }
}
