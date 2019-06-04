package com.tiza.tstar.op.util;


import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.CoreConnectionPNames;
import org.apache.http.util.EntityUtils;

import java.util.Iterator;
import java.util.Map;

/**
 * Description: HttpUtil
 * Author: DIYILIU
 * Update: 2019-05-22 16:15
 */
public class HttpUtil {

    /**
     * GET 请求
     *
     * @param url
     * @param param
     * @return
     * @throws Exception
     */
    public static String getForString(String url, Map<String, Object> param) throws Exception {
        HttpGet httpGet;
        if (param == null || param.isEmpty()) {
            httpGet = new HttpGet(url);
        } else {
            URIBuilder builder = new URIBuilder(url);
            for (Iterator<String> iterator = param.keySet().iterator(); iterator.hasNext(); ) {
                String key = iterator.next();
                String value = String.valueOf(param.get(key));
                builder.setParameter(key, value);
            }

            httpGet = new HttpGet(builder.build());
        }

        HttpClient client = new DefaultHttpClient();
        // 连接时间
        client.getParams().setParameter(CoreConnectionPNames.CONNECTION_TIMEOUT, 10000);
        // 数据传输时间
        client.getParams().setParameter(CoreConnectionPNames.SO_TIMEOUT, 3000);

        HttpResponse response = client.execute(httpGet);
        HttpEntity entity = response.getEntity();
        return EntityUtils.toString(entity, "UTF-8");
    }
}
