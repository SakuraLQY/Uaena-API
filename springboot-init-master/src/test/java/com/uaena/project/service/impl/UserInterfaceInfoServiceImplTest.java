package com.uaena.project.service.impl;

import com.uaena.project.service.UserInterfaceInfoService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;


/**
 * @Author: sakura
 * @Date: 2023/3/18 16:57
 * @Description: TODO
 * @Version 1.0
 */
@SpringBootTest
public class UserInterfaceInfoServiceImplTest {

    @Resource
    private UserInterfaceInfoService userInterfaceInfoService;
   @Test
     public void invokeCount() {
        boolean b =  userInterfaceInfoService.invokeCount(1L,1L);
       Assertions.assertTrue(b);
    }

    }

