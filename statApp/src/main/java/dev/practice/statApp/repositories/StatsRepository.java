package dev.practice.statApp.repositories;

import dev.practice.statApp.models.Stats;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StatsRepository extends JpaRepository<Stats, Long> {
}
