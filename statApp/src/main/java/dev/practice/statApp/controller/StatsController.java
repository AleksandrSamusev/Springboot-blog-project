package dev.practice.statApp.controller;

import dev.practice.statApp.models.StatisticRecord;
import dev.practice.statApp.models.StatisticResponse;
import dev.practice.statApp.services.StatsService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping("/api/v1")
public class StatsController {

    private final StatsService statsService;

    @PostMapping("/stats")
    public ResponseEntity<StatisticRecord> addStats(@Valid @RequestBody StatisticRecord statisticRecord) {
        return new ResponseEntity<>(statsService.addStats(statisticRecord), HttpStatus.CREATED);
    }

    @GetMapping("/stats")
    public List<StatisticRecord> getStats(
            @RequestParam LocalDateTime start,
            @RequestParam LocalDateTime end,
            @RequestParam(required = false) List<String> uris) {

        return statsService.getStats(start, end, uris);
    }
}
