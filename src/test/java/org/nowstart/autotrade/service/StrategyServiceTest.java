package org.nowstart.autotrade.service;

import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.nowstart.autotrade.data.dto.CandleDto;

@ExtendWith(MockitoExtension.class)
class StrategyServiceTest {

    @Mock
    private CandleCacheService candleCacheService;
    @Mock
    private EmaIndicator emaIndicator;
    @Mock
    private RsiIndicator rsiIndicator;

    @InjectMocks
    private StrategyService strategyService;

    private List<CandleDto> candles;

    @BeforeEach
    void setUp() {
        candles = new ArrayList<>();
        for (int i = 0; i < 30; i++) {
            candles.add(CandleDto.builder()
                    .code("KRW-BTC")
                    .tradePrice(100.0 + i)
                    .build());
        }
    }

    @Test
    void execute_ShouldCalculateIndicatorsWhenEnoughCandles() {
        // given
        String code = "KRW-BTC";
        when(candleCacheService.getCandlesOrDefault(code)).thenReturn(candles);
        
        List<Double> emaValues = new ArrayList<>();
        List<Double> rsiValues = new ArrayList<>();
        for(int i=0; i<30; i++) {
            emaValues.add(100.0);
            rsiValues.add(50.0);
        }
        
        when(emaIndicator.calculate(anyList())).thenReturn(emaValues);
        when(rsiIndicator.calculate(anyList())).thenReturn(rsiValues);

        // when
        strategyService.execute(code);

        // then
        verify(emaIndicator, atLeastOnce()).calculate(candles);
        verify(rsiIndicator, atLeastOnce()).calculate(candles);
    }
}
