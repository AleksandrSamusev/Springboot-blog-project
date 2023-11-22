package dev.practice.statApp.repositories;

import dev.practice.statApp.models.StatisticRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.List;

public interface StatsRepository extends JpaRepository<StatisticRecord, Long> {

    @Query("SELECT s FROM StatisticRecord s WHERE s.timestamp >= ?1 AND s.timestamp <= ?2 AND s.uri IN ?3")
    List<StatisticRecord> getStatsByStartEndNoUrisNotUnique(LocalDateTime start, LocalDateTime end, List<String> uris);

}
