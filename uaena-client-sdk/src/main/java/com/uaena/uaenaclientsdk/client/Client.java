package com.uaena.uaenaclientsdk.client;

import cn.hutool.core.util.RandomUtil;

import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONUtil;
import com.uaena.uaenaclientsdk.Model.User;


import java.util.HashMap;
import java.util.Map;
import static com.uaena.uaenaclientsdk.util.SignUtils.getSign;


/**
 * @Author: sakura
 * @Date: 2023/3/13 19:36
 * @Description: TODO
 * @Version 1.0
 */
public class Client {

    private String accessKey;
    private String secretKey;

    private static final String GATEWAY_HOST = "http://localhost:8090";

    public Client(String accessKey, String secretKey) {
        this.accessKey = accessKey;
        this.secretKey = secretKey;
    }

    public String getNameByGet(String name){
        HashMap<String,Object> param = new HashMap<>();
        param.put("name",name);
        String result = HttpUtil.get(GATEWAY_HOST+"/api/name/", param);
        System.out.println(result);
        return result;
    }

    public String getNameByPost(String name){
        HashMap<String,Object> param = new HashMap<>();
        param.put("name",name);
        String result = HttpUtil.post(GATEWAY_HOST+"api/name/", param);
        System.out.println(result);
        return result;
    }

    public Map<String,String>getHeader(String body){
        HashMap<String,String>hashMap = new HashMap<>();
        hashMap.put("accessKey",accessKey);
        hashMap.put("nonce", RandomUtil.randomNumbers(4));
        hashMap.put("timestamp",String.valueOf(System.currentTimeMillis()/100));
        hashMap.put("body",body);
//        hashMap.put("secretKey",secretKey);
        hashMap.put("sign",getSign(body,secretKey));
        return hashMap;
    }

    public String getUserNameByPost(User user){

        String json = JSONUtil.toJsonStr(user);
        HttpResponse resp= HttpRequest.post(GATEWAY_HOST+"/api/name/user")
                .addHeaders(getHeader(json))
                .body(json)
                .execute();
        System.out.println(resp.getStatus());
        String result = resp.body();
        System.out.println(result);
        return result;
    }
}
