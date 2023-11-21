package dev.practice.statApp.repositories;

import dev.practice.statApp.models.StatisticRecord;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StatsRepository extends JpaRepository<StatisticRecord, Long> {
}
