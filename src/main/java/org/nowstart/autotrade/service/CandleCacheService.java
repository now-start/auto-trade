package org.nowstart.autotrade.service;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;
import org.nowstart.autotrade.data.dto.CandleDto;
import org.springframework.stereotype.Service;

@Service
public class CandleCacheService {

    private final Cache<String, List<CandleDto>> candleCache = Caffeine.newBuilder()
            .expireAfterWrite(1, TimeUnit.DAYS)
            .maximumSize(100)
            .build();

    public void addCandle(String code, CandleDto candle) {
        candleCache.asMap().compute(code, (k, v) -> {
            List<CandleDto> candles = (v == null) ? new ArrayList<>() : v;
            candles.add(candle);
            // 최신 200개의 캔들만 유지 (지표 계산에 충분한 양)
            if (candles.size() > 200) {
                candles.remove(0);
            }
            return candles;
        });
    }

    public List<CandleDto> getCandles(String code) {
        return candleCache.getIfPresent(code);
    }

    public List<CandleDto> getCandlesOrDefault(String code) {
        return candleCache.asMap().getOrDefault(code, Collections.emptyList());
    }
}
