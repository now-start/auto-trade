package org.nowstart.autotrade.data.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CandleDto {
    private String type;
    private String code;
    @JsonProperty("candle_date_time_utc")
    private String candleDateTimeUtc;
    @JsonProperty("candle_date_time_kst")
    private String candleDateTimeKst;
    @JsonProperty("opening_price")
    private Double openingPrice;
    @JsonProperty("high_price")
    private Double highPrice;
    @JsonProperty("low_price")
    private Double lowPrice;
    @JsonProperty("trade_price")
    private Double tradePrice;
    @JsonProperty("candle_acc_trade_volume")
    private Double candleAccTradeVolume;
    @JsonProperty("candle_acc_trade_price")
    private Double candleAccTradePrice;
    private Double timestamp;
    @JsonProperty("stream_type")
    private Long streamType;
    private String realtime;
}
