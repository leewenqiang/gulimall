package com.atguigu.gulimall.gateway.config;

import com.alibaba.csp.sentinel.adapter.gateway.sc.callback.BlockRequestHandler;
import com.alibaba.csp.sentinel.adapter.gateway.sc.callback.GatewayCallbackManager;
import com.alibaba.fastjson.JSON;
import com.atguigu.common.exception.BizCodeEnum;
import com.atguigu.common.utils.R;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.ServerResponse;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

/**
 * @ClassName SentinelgateWayConfig
 * @Description TODO
 * @Author lwq
 * @Date 2021/2/3 10:08
 * @Version 1.0
 */
@Configuration
public class SentinelgateWayConfig {

    public SentinelgateWayConfig(){
        GatewayCallbackManager.setBlockHandler(new BlockRequestHandler() {

            /**
             * 限流请求
             * @param serverWebExchange
             * @param throwable
             * @return
             */
            @Override
            public Mono<ServerResponse> handleRequest(ServerWebExchange serverWebExchange, Throwable throwable) {

                R error = R.error(BizCodeEnum.TOO_MANY_RQUEST.getCode(), BizCodeEnum.TOO_MANY_RQUEST.getMsg());
                String jsonString = JSON.toJSONString(error);
                Mono<ServerResponse> body = ServerResponse.ok().body(Mono.just(jsonString), String.class);
                return body;
            }
        });
    }

}
