package dev.practice.statApp.repositories;

import dev.practice.statApp.models.StatisticRecord;
import dev.practice.statApp.models.StatisticResponse;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.List;

public interface StatsRepository extends JpaRepository<StatisticRecord, Long> {

    @Query("select new dev.practice.statApp.models.StatisticResponse(s.app, s.uri, CAST(COUNT(s.id) as int))" +
            " from StatisticRecord as s where s.timestamp >= :start and s.timestamp <= :end" +
            " group by s.app, s.uri")
    List<StatisticResponse> getStatsByStartEndNoUrisNotUnique(LocalDateTime start, LocalDateTime end);

    @Query("select new dev.practice.statApp.models.StatisticResponse(s.app, s.uri, CAST(COUNT(s.id) as int))" +
            " from StatisticRecord as s where s.timestamp >= :start and s.timestamp <= :end" +
            " group by s.app, s.uri, s.ip")
    List<StatisticResponse> getStatsByStartEndNoUrisUnique(LocalDateTime start, LocalDateTime end);

    @Query("select new dev.practice.statApp.models.StatisticResponse(s.app, s.uri, CAST(COUNT(s.id) as int))" +
            " from StatisticRecord as s where s.timestamp >= :start and s.timestamp <= :end" +
            " and s.uri in :uris group by s.app, s.uri")
    List<StatisticResponse> getStatsByStartEndWithUrisNoUnique(LocalDateTime start,
                                                               LocalDateTime end,
                                                               List<String> uris);

    @Query("select new dev.practice.statApp.models.StatisticResponse(s.app, s.uri, CAST(COUNT(s.id) as int))" +
            " from StatisticRecord as s where s.timestamp >= :start and s.timestamp <= :end" +
            " and s.uri in :uris group by s.app, s.uri, s.ip")
    List<StatisticResponse> getStatsByStartEndWithUrisUnique(LocalDateTime start,
                                                             LocalDateTime end,
                                                             List<String> uris);
}
