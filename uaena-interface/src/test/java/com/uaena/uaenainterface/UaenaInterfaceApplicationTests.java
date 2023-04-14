package com.uaena.uaenainterface;


import com.uaena.uaenaclientsdk.Model.User;
import com.uaena.uaenaclientsdk.client.Client;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;

@SpringBootTest
class UaenaInterfaceApplicationTests {

    @Test
    void contextLoads() {
    }

    @Resource
    private Client client;
    @Test
    void test(){
        String sakkura = client.getNameByGet("sakura");
        System.out.println(sakkura);
        User user = new User();
        user.setName("sakura");
        String userNameByPost = client.getUserNameByPost(user);
        System.out.println(userNameByPost);

    }

}
