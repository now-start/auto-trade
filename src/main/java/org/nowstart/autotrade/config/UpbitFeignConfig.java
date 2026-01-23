package org.nowstart.autotrade.config;

import feign.RequestInterceptor;
import java.util.HashMap;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.nowstart.autotrade.service.UpbitAuthService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class UpbitFeignConfig {

    private final UpbitAuthService upbitAuthService;

    @Bean
    public RequestInterceptor requestInterceptor() {
        return requestTemplate -> {
            Map<String, Object> params = new HashMap<>();
            requestTemplate.queries().forEach((key, values) -> {
                if (values != null && !values.isEmpty()) {
                    params.put(key, values.iterator().next());
                }
            });

            String jwtToken = upbitAuthService.createJwt(params);
            requestTemplate.header("Authorization", "Bearer " + jwtToken);
        };
    }
}
