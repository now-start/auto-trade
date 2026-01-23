package org.nowstart.autotrade.service;

import java.util.ArrayList;
import java.util.List;
import org.nowstart.autotrade.data.dto.CandleDto;
import org.springframework.stereotype.Service;

@Service
public class RsiIndicator implements Indicator<List<Double>> {

    private final int period;

    public RsiIndicator() {
        this.period = 14;
    }

    public RsiIndicator(int period) {
        this.period = period;
    }

    @Override
    public List<Double> calculate(List<CandleDto> candles) {
        List<Double> result = new ArrayList<>();
        if (candles.size() < period) {
            for (int i = 0; i < candles.size(); i++) {
                result.add(null);
            }
            return result;
        }

        List<Double> gains = new ArrayList<>();
        List<Double> losses = new ArrayList<>();

        for (int i = 1; i < candles.size(); i++) {
            double diff = candles.get(i).getTradePrice() - candles.get(i - 1).getTradePrice();
            gains.add(Math.max(0, diff));
            losses.add(Math.max(0, -diff));
        }

        double avgGain = 0;
        double avgLoss = 0;

        for (int i = 0; i < candles.size(); i++) {
            if (i < period) {
                result.add(null);
                if (i > 0) {
                    avgGain += gains.get(i - 1);
                    avgLoss += losses.get(i - 1);
                }
                if (i == period - 1) {
                    avgGain /= period;
                    avgLoss /= period;
                }
                continue;
            }

            avgGain = (avgGain * (period - 1) + gains.get(i - 1)) / period;
            avgLoss = (avgLoss * (period - 1) + losses.get(i - 1)) / period;

            if (avgLoss == 0) {
                result.add(100.0);
            } else {
                double rs = avgGain / avgLoss;
                result.add(100.0 - (100.0 / (1 + rs)));
            }
        }

        return result;
    }
}
