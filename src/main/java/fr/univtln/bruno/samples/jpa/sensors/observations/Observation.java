package fr.univtln.bruno.samples.jpa.sensors.observations;

import fr.univtln.bruno.samples.jpa.sensors.devices.Sensor;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Setter
@Getter
@ToString
@AllArgsConstructor(staticName = "of")
@NoArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)

@Entity
@Table(name = "OBSERVATION")
public class Observation {
    @Id
    @Column(name = "ID")
    private final UUID uuid = UUID.randomUUID();

    @Column(name = "RESULT_DATETIME")
    private LocalDateTime resultDateTime;

    @ManyToOne
    @JoinColumn(name = "SOURCE")
    private Sensor source;

    @Embedded
    private Result result;

    @ManyToOne
    private FeatureOfInterest featureOfInterest;

    @ManyToOne
    private ObservableProperty observableProperty;
}

