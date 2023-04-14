package com.uaena.uaenaapigateway;

import com.uaena.common.model.entity.InterfaceInfo;
import com.uaena.common.model.entity.User;
import com.uaena.common.service.InnerInterfaceInfoService;
import com.uaena.common.service.InnerUserInterfaceInfoService;
import com.uaena.common.service.InnerUserService;
import com.uaena.uaenaclientsdk.util.SignUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboReference;
import org.reactivestreams.Publisher;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferFactory;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.http.server.reactive.ServerHttpResponseDecorator;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Slf4j
@Component
public class CustomGlobalFilter implements GlobalFilter, Ordered {

    private static final List<String> IP_WHITE_LIST = Arrays.asList("127.0.0.1");

    private static final String INTERFACE_HOST = "http://localhost:8090";

    private static final String BACKEND_HOST = "http://localhost:7777";

    @DubboReference
    private InnerUserService innerUserService;

    @DubboReference
    private InnerUserInterfaceInfoService interfaceUserInfoService;

    @DubboReference
    private InnerInterfaceInfoService interfaceInfoService;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        // 1. 请求日志
        ServerHttpRequest request = exchange.getRequest();
        // 这里简单处理，只要是普通的请求就做转发操作即可
        String requestPath = request.getPath().value();
        String prefix = requestPath.substring(0, 9);
        if ("/apiUser/".equals(prefix)) {
            return chain.filter(exchange);
        }
        String path = INTERFACE_HOST + requestPath;
        String method = request.getMethod().toString();
        log.info("请求唯一标识：" + request.getId());
        log.info("请求路径：" + path);
        log.info("请求方法：" + method);
        log.info("请求参数：" + request.getQueryParams());
        String sourceAddress = request.getLocalAddress().getHostString();
        log.info("请求来源地址：" + sourceAddress);
        log.info("请求来源地址：" + request.getRemoteAddress());
        ServerHttpResponse response = exchange.getResponse();
        //4.校验AK和SK
        HttpHeaders headers = request.getHeaders();
        String accessKey = headers.getFirst("accessKey");//检查用户是否分配了
        String timeStamp = headers.getFirst("timestamp");
        String sign = headers.getFirst("sign");
        String nonce = headers.getFirst("nonce");
        String body = headers.getFirst("body");
        //校验是否分配了ak,sk
        User invokeUser = null;
        try{
            innerUserService.getInvokeUser(accessKey);
        }catch (Exception e){
            log.error("getInvokeUser error", e);
        }
        if(invokeUser==null){
            return handleNoAuth(response);
        }
        if(nonce!=null && Long.parseLong(nonce)>10000L){
            return handleNoAuth(response);
        }
        //验证时间和当前时间
        Long currentTime = System.currentTimeMillis()/1000;
        final Long Five_Minute = 60 * 5L;
        if(timeStamp!=null && (currentTime-Long.parseLong(timeStamp))>=Five_Minute){
            return handleNoAuth(response);
        }
        //从数据库中查询到secretKey进行签名认证
        String secretKey = invokeUser.getSecretKey();
        String serverSign = SignUtils.getSign(body,secretKey);
        if(sign.equals(serverSign)){
            return handleNoAuth(response);
        }
        //todo 请求的接口是否存在？以及请求方法是否匹配
        InterfaceInfo interfaceInfo = null;
        try {
            interfaceInfoService.getInterfaceInfo(path, method);
        } catch (Exception e) {
            log.error("getInterfaceInfo error",e);
        }
        return handleResponse(exchange,chain, interfaceInfo.getId(), invokeUser.getId());

    }

    @Override
    public int getOrder() {
        return -1;
    }

    public Mono<Void>handleNoAuth(ServerHttpResponse response){
        response.setStatusCode(HttpStatus.FORBIDDEN);
        return response.setComplete();
    }

    public Mono<Void>handleNoSuccess(ServerHttpResponse response){
        response.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR);
        return response.setComplete();
    }

    public Mono<Void> handleResponse(ServerWebExchange exchange, GatewayFilterChain chain,long interfaceInfoId,long userId) {
        try {
            ServerHttpResponse originalResponse = exchange.getResponse();
            // 缓存数据的工厂
            DataBufferFactory bufferFactory = originalResponse.bufferFactory();
            // 拿到响应码
            HttpStatus statusCode = originalResponse.getStatusCode();
            if (statusCode == HttpStatus.OK) {
                // 装饰，增强能力
                ServerHttpResponseDecorator decoratedResponse = new ServerHttpResponseDecorator(originalResponse) {
                    // 等调用完转发的接口后才会执行
                    @Override
                    public Mono<Void> writeWith(Publisher<? extends DataBuffer> body) {
                        log.info("body instanceof Flux: {}", (body instanceof Flux));
                        if (body instanceof Flux) {
                            Flux<? extends DataBuffer> fluxBody = Flux.from(body);
                            // 往返回值里写数据
                            // 拼接字符串
                            return super.writeWith(
                                    fluxBody.map(dataBuffer -> {
                                         //调用成功，接口调用次数 + 1 invokeCount
                                        try {
                                            interfaceUserInfoService.invokeCount(interfaceInfoId, userId);
                                        } catch (Exception e) {
                                            log.error("invokeCount error", e);
                                        }
                                        byte[] content = new byte[dataBuffer.readableByteCount()];
                                        dataBuffer.read(content);
                                        DataBufferUtils.release(dataBuffer);
                                        // 构建日志
                                        StringBuilder sb2 = new StringBuilder(200);
                                        List<Object> rspArgs = new ArrayList<>();
                                        rspArgs.add(originalResponse.getStatusCode());
                                        String data = new String(content, StandardCharsets.UTF_8);
                                        sb2.append(data);
                                        // 打印日志
                                        log.info("响应结果：" + data);
                                        return bufferFactory.wrap(content);
                                    }));
                        } else {
                            // 8. 调用失败，返回一个规范的错误码
                            log.error("响应异常code: {}", getStatusCode());
                        }
                        return super.writeWith(body);
                    }
                };
                // 设置 response 对象为装饰过的
                return chain.filter(exchange.mutate().response(decoratedResponse).build());
            }
            // 降级处理返回数据
            return chain.filter(exchange);
        } catch (Exception e) {
            log.error("网关处理响应异常" + e);
            return chain.filter(exchange);
        }
    }
}
