package dev.practice.statApp.controller;

import dev.practice.statApp.models.Stats;
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
    public ResponseEntity<Stats> addStats(@Valid @RequestBody Stats stats) {
        return new ResponseEntity<>(statsService.addStats(stats), HttpStatus.CREATED);
    }
}
