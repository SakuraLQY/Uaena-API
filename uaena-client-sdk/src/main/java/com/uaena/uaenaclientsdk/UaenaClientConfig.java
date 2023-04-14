package com.uaena.uaenaclientsdk;

import com.uaena.uaenaclientsdk.client.Client;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

/**
 * @Author: sakura
 * @Date: 2023/3/14 15:39
 * @Description: 声明一个配置类
 * @Version 1.0
 */
@Data
@Configuration
@ConfigurationProperties("uaena.client")
@ComponentScan
public class UaenaClientConfig {

    private String accessKey;

    private String secretKey;

    @Bean
    public Client uaenaClient() {
        return new Client(accessKey,secretKey);
    }
}
