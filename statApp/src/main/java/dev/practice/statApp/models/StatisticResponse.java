package dev.practice.statApp.models;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class StatisticResponse {
    private String app;
    private String uri;
    private Long numberOfRecords;
}
