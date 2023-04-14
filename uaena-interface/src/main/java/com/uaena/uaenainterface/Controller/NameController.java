package com.uaena.uaenainterface.Controller;

import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import com.uaena.uaenainterface.Model.User;
import com.uaena.uaenainterface.util.SignUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

/**
 * @Author: sakura
 * @Date: 2023/3/13 18:56
 * @Description: 名称API
 * @Version 1.0
 */
@RestController
@RequestMapping("/name")
public class NameController {
    @GetMapping("/name")
    public String getNameByGet(String name,HttpServletRequest request){
        String header = request.getHeader("sakura");
        System.out.println(header);
        return "Get 你的名字是"+name;
    }

    @PostMapping("/")
    public String getNameByPost(@RequestParam String name){
        return "Post"+"你的名字"+name;
    }

    @PostMapping("/user")
    public String getUerNameByPost(@RequestBody User user, HttpServletRequest request){
        String accessKey = request.getHeader("accessKey");
        //要去数据库进行查询
        String nonce = request.getHeader("nonce");

        String timestamp = request.getHeader("timestamp");
        String sign = request.getHeader("sign");
        String body = request.getHeader("body");
//        String secretKey = request.getHeader("secretKey");
        //进行权限的校验
        if(!accessKey.equals("sakura")){
            throw new RuntimeException("权限不狗");
        }
        if(Long.parseLong(nonce)>10000){
            throw new RuntimeException("无权限");
        }
      String serverSign = SignUtils.getSign(body,"abcdefgh");
        if(!sign.equals(serverSign)){
            throw new RuntimeException("....");
        }

        return "POST 用户名"+user.getName();
    }
}
