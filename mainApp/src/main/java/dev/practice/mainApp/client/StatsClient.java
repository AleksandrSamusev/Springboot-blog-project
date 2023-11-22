package dev.practice.mainApp.client;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import dev.practice.mainApp.models.StatisticRecord;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.DefaultUriBuilderFactory;

import java.time.LocalDateTime;
import java.util.List;
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

    public List<StatisticRecord> getStats(LocalDateTime start,
                                            LocalDateTime end,
                                            List<String> uris) throws JsonProcessingException {

        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        StringBuilder builder = new StringBuilder(String.format("/api/v1/stats?start=%s&end=%s", start, end));

        if (uris != null) {
            builder.append(String.format("&uris=%s", String.join(",", uris)));
        }

        log.info("request parameters: start - {}, end - {}, uris - {}", start, end, uris);
        ResponseEntity<String> response = restTemplate.getForEntity(builder.toString(), String.class);
        System.out.println(response.getBody());
        log.info("response status: {}", response.getStatusCode());
        String jsonString = response.getBody();

        return mapper.readValue(jsonString, new TypeReference<>() {
        });
    }
}
