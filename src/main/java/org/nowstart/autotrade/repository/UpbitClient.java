package org.nowstart.autotrade.repository;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "upbitClient", url = "https://api.upbit.com/v1")
public interface UpbitClient {

    @PostMapping("/orders")
    Object placeOrder(
            @RequestParam("market") String market,
            @RequestParam("side") String side,
            @RequestParam("volume") String volume,
            @RequestParam("price") String price,
            @RequestParam("ord_type") String ordType
    );
}
