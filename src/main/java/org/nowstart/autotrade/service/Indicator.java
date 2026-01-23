package org.nowstart.autotrade.service;

import java.util.List;
import org.nowstart.autotrade.data.dto.CandleDto;

public interface Indicator<T> {
    T calculate(List<CandleDto> candles);
    default String name() {
        return getClass().getSimpleName();
    }
}
