package dev.practice.mainApp.client;

import dev.practice.mainApp.models.StatisticRecord;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.DefaultUriBuilderFactory;

@Component
@Slf4j
public class StatsClient {

    private final RestTemplate restTemplate;

    public StatsClient(@Value("http://localhost:9090") String serverUrl,
                       RestTemplateBuilder builder) {

        this.restTemplate = builder.uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl))
                .build();
    }

    public void addStats(StatisticRecord statisticRecord) {
        log.info("Save statistics: app = {}, uri = {}, ip = {}", statisticRecord.getServiceName(),
                statisticRecord.getUri(), statisticRecord.getIp());
        restTemplate.postForObject("/api/v1/stats", statisticRecord, String.class);
    }


}
