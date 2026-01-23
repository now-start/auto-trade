package org.nowstart.autotrade.service;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.nowstart.autotrade.data.dto.CandleDto;

class EmaIndicatorTest {

    @Test
    void calculate() {
        // given
        int period = 5;
        EmaIndicator emaIndicator = new EmaIndicator(period);

        List<CandleDto> candles = new ArrayList<>();
        for (int i = 1; i <= 10; i++) {
            candles.add(CandleDto.builder().tradePrice((double) i).build());
        }

        // when
        List<Double> result = emaIndicator.calculate(candles);

        // then
        assertThat(result).hasSize(10);
        assertThat(result.get(period - 2)).isNull();
        assertThat(result.get(period - 1)).isEqualTo(3.0);
        System.out.println("[DEBUG_LOG] EMA Result: " + result);
    }
}
