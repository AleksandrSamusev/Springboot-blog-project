package dev.practice.statApp.services;

import dev.practice.statApp.models.StatisticRecord;
import dev.practice.statApp.models.StatisticResponse;

import java.time.LocalDateTime;
import java.util.List;

public interface StatsService {
    StatisticRecord addStats(StatisticRecord statisticRecord);

    List<StatisticResponse> getStats(LocalDateTime start,
                                     LocalDateTime end,
                                     List<String> uris,
                                     Boolean unique);
}
