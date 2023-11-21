package dev.practice.statApp.services.impl;

import dev.practice.statApp.models.Stats;
import dev.practice.statApp.repositories.StatsRepository;
import dev.practice.statApp.services.StatsService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class StatsServiceImpl implements StatsService {
    private final StatsRepository statsRepository;

    @Override
    public Stats addStats(Stats stats) {
       return statsRepository.save(stats);
    }
}
