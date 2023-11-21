package dev.practice.statApp.controller;

import dev.practice.statApp.models.StatisticRecord;
import dev.practice.statApp.services.StatsService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@AllArgsConstructor
@RequestMapping("/api/v1")
public class StatsController {

    private final StatsService statsService;

    @PostMapping("/stats")
    public ResponseEntity<StatisticRecord> addStats(@Valid @RequestBody StatisticRecord statisticRecord) {
        return new ResponseEntity<>(statsService.addStats(statisticRecord), HttpStatus.CREATED);
    }
}
