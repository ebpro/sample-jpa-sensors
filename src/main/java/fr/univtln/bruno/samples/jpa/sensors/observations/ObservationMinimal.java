package fr.univtln.bruno.samples.jpa.sensors.observations;

import jakarta.persistence.Column;

import java.time.LocalDateTime;

public record ObservationMinimal(@Column(name = "RESULT_DATETIME")
                                 LocalDateTime resultDateTime,
                                 @Column(name = "MEASURE_VALUE")
                                 Number value,
                                 @Column(name = "MEASURE_UNIT")
                                 String unit) {
}
