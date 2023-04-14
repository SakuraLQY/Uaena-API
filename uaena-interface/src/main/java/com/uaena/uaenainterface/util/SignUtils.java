package com.uaena.uaenainterface.util;

import cn.hutool.crypto.digest.DigestAlgorithm;
import cn.hutool.crypto.digest.Digester;

/**
 * @Author: sakura
 * @Date: 2023/3/13 22:19
 * @Description: TODO
 * @Version 1.0
 */
public class SignUtils {
    /***
     * 生成签名
     * @param body
     * @param secretKey
     */
    public static String getSign(String body,String secretKey){
        Digester md5 = new Digester(DigestAlgorithm.SHA256);
        String testStr = body+"."+secretKey;
// 5393554e94bf0eb6436f240a4fd71282
        return md5.digestHex(testStr);
    }
}
