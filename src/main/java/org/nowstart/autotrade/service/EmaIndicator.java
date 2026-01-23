package org.nowstart.autotrade.service;

import java.util.ArrayList;
import java.util.List;
import org.nowstart.autotrade.data.dto.CandleDto;
import org.springframework.stereotype.Service;

@Service
public class EmaIndicator implements Indicator<List<Double>> {

    private final int period;

    public EmaIndicator() {
        this.period = 14;
    }

    public EmaIndicator(int period) {
        this.period = period;
    }

    @Override
    public List<Double> calculate(List<CandleDto> candles) {
        List<Double> closes = candles.stream()
                .map(CandleDto::getTradePrice)
                .toList();

        List<Double> result = new ArrayList<>();
        double alpha = 2.0 / (period + 1);

        Double prevEma = null;

        for (int i = 0; i < closes.size(); i++) {
            double price = closes.get(i);

            if (i + 1 < period) {
                result.add(null);
                continue;
            }

            if (prevEma == null) {
                prevEma = closes.subList(0, period)
                        .stream()
                        .mapToDouble(Double::doubleValue)
                        .average()
                        .orElse(0);
            } else {
                prevEma = price * alpha + prevEma * (1 - alpha);
            }

            result.add(prevEma);
        }

        return result;
    }
}
