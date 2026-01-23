package org.nowstart.autotrade.service;

import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.nowstart.autotrade.repository.UpbitClient;
import org.nowstart.autotrade.data.dto.CandleDto;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class StrategyService {

    private final CandleCacheService candleCacheService;
    private final EmaIndicator emaIndicator;
    private final RsiIndicator rsiIndicator;
    private final UpbitClient upbitClient;

    public void execute(String code) {
        List<CandleDto> candles = candleCacheService.getCandlesOrDefault(code);

        if (candles.size() < 20) {
            return;
        }

        List<Double> emaValues = emaIndicator.calculate(candles);
        List<Double> rsiValues = rsiIndicator.calculate(candles);

        double lastClose = candles.getLast().getTradePrice();
        Double lastEma = emaValues.getLast();
        Double lastRsi = rsiValues.getLast();

        if (lastEma == null || lastRsi == null) {
            return;
        }

        log.info("Code: {}, Close: {}, EMA: {}, RSI: {}", code, lastClose, lastEma, lastRsi);

        // 간단한 전략 예시: RSI < 30 이고 현재가가 EMA보다 낮으면 매수 (과매도 상황)
        if (lastRsi < 30 && lastClose < lastEma) {
            log.info(">>> BUY Signal detected for {}", code);
            try {
                // 예시용 파라미터. 실제로는 자산 조회 후 시장가 매수 등 정교한 로직 필요
                upbitClient.placeOrder(code, "bid", "0.01", null, "price");
            } catch (Exception e) {
                log.error("Failed to place buy order for {}", code, e);
            }
        }
        // RSI > 70 이고 현재가가 EMA보다 높으면 매도 (과매수 상황)
        else if (lastRsi > 70 && lastClose > lastEma) {
            log.info(">>> SELL Signal detected for {}", code);
            try {
                // 예시용 파라미터. 실제로는 보유 수량 확인 후 시장가 매도 등 정교한 로직 필요
                upbitClient.placeOrder(code, "ask", "0.01", null, "market");
            } catch (Exception e) {
                log.error("Failed to place sell order for {}", code, e);
            }
        }
    }
}
