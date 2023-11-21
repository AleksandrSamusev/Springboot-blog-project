package dev.practice.statApp.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "statistic_records")
public class StatisticRecord {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "statistic_record_id")
    private Long statisticRecordId;

    @Column(name = "service_name", nullable = false)
    @NotBlank(message = "Service name cannot be blank")
    private String serviceName = "mainApp";

    @Column(name = "ip", nullable = false)
    @NotBlank(message = "ip address cannot be blank")
    private String ip;

    @Column(name = "uri", nullable = false)
    @NotBlank(message = "Uri cannot be blank")
    private String uri;

    @Column(name = "timestamp", nullable = false)
    @NotNull(message = "Timestamp cannot be null")
    private LocalDateTime timestamp = LocalDateTime.now();
}
